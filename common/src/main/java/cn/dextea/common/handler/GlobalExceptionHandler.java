package cn.dextea.common.handler;

import cn.dextea.common.dto.DexteaApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数校验失败异常
     * @param e 异常信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DexteaApiResponse<Object> handleValidationException(MethodArgumentNotValidException e) {
        String msg=e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.error("请求参数错误: {}", msg);
        return DexteaApiResponse.fail(msg);
    }

    /**
     * 参数错误异常
     * @param e 异常信息
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public DexteaApiResponse<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("请求参数错误: {}", e.getMessage());
        return DexteaApiResponse.fail(e.getMessage());
    }

    /**
     * 权限异常
     * @param e 异常信息
     */
    @ExceptionHandler(IllegalAccessException.class)
    public DexteaApiResponse<Object> handleIllegalAccessException(IllegalAccessException e) {
        log.error("权限错误: {}", e.getMessage());
        return DexteaApiResponse.unauthorized(e.getMessage());
    }

    /**
     * 404 - NotFound
     * @param e 异常信息
     */
    @ExceptionHandler(NotFoundException.class)
    public DexteaApiResponse<Object> handleNotFoundException(NotFoundException e) {
        log.error("404 NotFound: {}", e.getMessage());
        return DexteaApiResponse.notFound(e.getMessage());
    }

    /**
     * 请求参数异常
     * @param e 异常信息
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public DexteaApiResponse<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("请求参数错误: {}", e.getMessage());
        return DexteaApiResponse.fail("参数错误");
    }

    /**
     * 兜底异常处理
     * @param e 异常信息
     */
    @ExceptionHandler(Exception.class)
    public DexteaApiResponse<Object> handleException(Exception e) {
        log.error("服务器内部异常", e);
        return DexteaApiResponse.serverError();
    }
}
package cn.dextea.common.handler;

import cn.dextea.common.dto.ApiResponse;
import com.alibaba.fastjson2.JSONObject;
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
    public ApiResponse handleValidationException(MethodArgumentNotValidException e) {
        String msg=e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.error("请求参数错误: {}", msg);
        return ApiResponse.badRequest(msg);
    }

    /**
     * 参数错误异常
     * @param e 异常信息
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("请求参数错误: {}", e.getMessage());
        return ApiResponse.badRequest(e.getMessage());
    }

    /**
     * 权限异常
     * @param e 异常信息
     */
    @ExceptionHandler(IllegalAccessException.class)
    public ApiResponse handleIllegalAccessException(IllegalAccessException e) {
        log.error("权限错误: {}", e.getMessage());
        return ApiResponse.forbidden(e.getMessage());
    }

    /**
     * 资源不存在
     * @param e 异常信息
     */
    @ExceptionHandler(NotFoundException.class)
    public ApiResponse handleNotFoundException(NotFoundException e) {
        log.error("资源不存在: {}", e.getMessage());
        return ApiResponse.notFound(e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("请求参数错误: {}", e.getMessage());
        return ApiResponse.badRequest("请求参数错误");
    }

    /**
     * 兜底异常处理
     * @param e 异常信息
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse handleException(Exception e) {
        log.error("服务器内部异常", e);
        return ApiResponse.serverError();
    }
}
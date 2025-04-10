package cn.dextea.common.handler;

import cn.dextea.common.model.common.DexteaApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 参数校验错误
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public DexteaApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        String msg=e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        log.error("参数错误: {}", e.getBindingResult().getFieldErrors());
        return DexteaApiResponse.fail(msg);
    }

    // 请求缺少参数
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public DexteaApiResponse<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e){
        log.error("缺少参数: {}", e.getMessage());
        return DexteaApiResponse.fail("缺少参数");
    }

    // 请求参数错误
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public DexteaApiResponse<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("参数错误", e);
        return DexteaApiResponse.fail("参数错误");
    }

    // 文件体积过大
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<DexteaApiResponse<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("文件体积过大：{}", e.getMessage());
        return ResponseEntity.badRequest().body(DexteaApiResponse.fail("文件体积过大"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public DexteaApiResponse<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("请求方法不支持：{}", e.getMessage());
        return DexteaApiResponse.fail("global.request.method.not.supported","请求方法错误");
    }

    // 其他异常
    @ExceptionHandler(Exception.class)
    public DexteaApiResponse<Object> handleException(Exception e) {
        log.error("服务器内部异常", e);
        return DexteaApiResponse.serverError();
    }
}
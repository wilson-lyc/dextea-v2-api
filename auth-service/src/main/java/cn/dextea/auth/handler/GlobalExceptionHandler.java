package cn.dextea.auth.handler;

import cn.dextea.common.dto.ApiResponse;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse handleValidationException(MethodArgumentNotValidException e) {
        JSONObject errors = new JSONObject();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.error("请求参数错误: {}", errors);
        return ApiResponse.badRequest();
    }
    @ExceptionHandler(Exception.class)
    public ApiResponse handleException(Exception e) {
        log.error("服务器内部异常",e);
        return ApiResponse.serverError();
    }
}
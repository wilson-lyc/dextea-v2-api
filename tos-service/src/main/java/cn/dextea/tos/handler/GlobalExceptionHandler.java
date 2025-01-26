package cn.dextea.tos.handler;

import cn.dextea.common.dto.ApiResponse;
import com.alibaba.fastjson2.JSONObject;
import com.volcengine.tos.TosClientException;
import com.volcengine.tos.TosServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 请求参数错误
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse handleValidationException(MethodArgumentNotValidException e) {
        JSONObject errors = new JSONObject();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.error("请求参数错误: {}", errors);
        return ApiResponse.badRequest();
    }

    // TOS客户端异常
    @ExceptionHandler(TosClientException.class)
    public ApiResponse handleTosClientException(TosClientException e) {
        log.error("TOS客户端异常: {}", e.getMessage());
        if (e.getCause() != null) {
            e.getCause().printStackTrace();
        }
        return ApiResponse.serverError("TOS客户端异常");
    }

    // TOS服务端异常 
    @ExceptionHandler(TosServerException.class)
    public ApiResponse handleTosServerException(TosServerException e) {
        log.error("TOS服务端异常: StatusCode={}, Code={}, Message={}, RequestID={}",e.getStatusCode(),e.getCode(),e.getMessage(),e.getRequestID());
        return ApiResponse.serverError("TOS服务端异常");
    }

    // 兜底
    @ExceptionHandler(Exception.class)
    public ApiResponse handleException(Exception e) {
        log.error("服务器异常",e);
        return ApiResponse.serverError();
    }
}
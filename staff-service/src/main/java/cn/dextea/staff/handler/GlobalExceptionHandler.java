package cn.dextea.staff.handler;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.ResponseDTO;
import cn.dextea.common.exception.MySQLException;
import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse handleValidationException(MethodArgumentNotValidException e) {
        JSONObject errors = new JSONObject();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.error("参数错误: {}", errors);
        return ApiResponse.badRequest("参数错误");
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse handleValidationException(ConstraintViolationException e) {
        log.error("内部参数异常: {}", e.getMessage());
        return ApiResponse.serverError("服务器内部参数异常");
    }
    @ExceptionHandler(MySQLException.class)
    public ApiResponse handleMySQLException(MySQLException e) {
        log.error("MySQL异常: {}", e.getMessage(),e);
        return ApiResponse.serverError("数据库异常");
    }
    @ExceptionHandler(TypeMismatchException.class)
    public ApiResponse handleTypeMismatchException(TypeMismatchException e) {
        log.error("参数类型异常: {}", e.getMessage());
        return ApiResponse.badRequest("参数类型异常");
    }
    @ExceptionHandler(Exception.class)
    public ApiResponse handleException(Exception e) {
        log.error("服务器内部异常: {}",e.getMessage());
        return ApiResponse.serverError("服务器内部异常");
    }
}
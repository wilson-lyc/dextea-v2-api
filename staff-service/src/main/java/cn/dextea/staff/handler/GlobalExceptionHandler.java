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
        log.error("Invalid parameter: {}", errors);
        return ApiResponse.badRequest("Invalid parameter");
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse handleValidationException(ConstraintViolationException e) {
        log.error("Internal parameter error: {}", e.getMessage());
        return ApiResponse.serverError("Internal parameter error");
    }
    @ExceptionHandler(MySQLException.class)
    public ApiResponse handleMySQLException(MySQLException e) {
        log.error("MySQL error: {}", e.getMessage(),e);
        return ApiResponse.serverError("Internal database error");
    }
    @ExceptionHandler(TypeMismatchException.class)
    public ApiResponse handleTypeMismatchException(TypeMismatchException e) {
        log.error("Type mismatch error: {}", e.getMessage());
        return ApiResponse.badRequest("Type mismatch error");
    }
    @ExceptionHandler(Exception.class)
    public ApiResponse handleException(Exception e) {
        log.error("Internal server error: {}",e.getMessage());
        return ApiResponse.serverError("Internal server error");
    }
}
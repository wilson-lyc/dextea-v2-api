package cn.dextea.staff.handler;

import cn.dextea.common.dto.ResponseDTO;
import cn.dextea.common.exception.MySQLException;
import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseDTO handleValidationException(MethodArgumentNotValidException e) {
        JSONObject errors = new JSONObject();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseDTO(400, "Invalid parameter",errors);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseDTO handleValidationException(ConstraintViolationException e) {
        log.error("Internal server parameter error: {}", e.getMessage());
        return new ResponseDTO(500, "Internal server parameter error");
    }
    @ExceptionHandler(MySQLException.class)
    public ResponseDTO handleMySQLException(MySQLException e) {
        log.error("MySQL error: {}", e.getMessage());
        return new ResponseDTO(500, "Internal server error");
    }
    @ExceptionHandler(Exception.class)
    public ResponseDTO handleException(Exception e) {
        log.error("Internal server error",e);
        return new ResponseDTO(500, "Internal server error");
    }
}
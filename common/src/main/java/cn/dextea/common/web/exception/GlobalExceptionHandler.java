package cn.dextea.common.web.exception;

import cn.dextea.common.code.GlobalErrorCode;
import cn.dextea.common.code.ResponseCode;
import cn.dextea.common.web.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Objects;

/**
 * Spring Web 全局异常处理器，负责兜底处理通用的参数、数据库和运行时异常。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 `@Valid` 触发的请求体参数校验异常。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String msg = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        log.warn("Validation failed: {}", msg);
        return ApiResponse.fail(ResponseCode.FAIL.getCode(), msg);
    }

    /**
     * 处理表单绑定阶段的参数校验异常。
     */
    @ExceptionHandler(BindException.class)
    public ApiResponse<Void> handleBindException(BindException e) {
        String msg = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        log.warn("Bind failed: {}", msg);
        return ApiResponse.fail(ResponseCode.FAIL.getCode(), msg);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    /**
     * 处理缺少参数、参数类型不匹配和请求体不可读等 bad request 场景。
     */
    public ApiResponse<Void> handleBadRequestException(Exception e) {
        log.warn("Bad request: {}", e.getMessage());
        return ApiResponse.fail(ResponseCode.FAIL.getCode(), GlobalErrorCode.PARAM_ERROR.getMsg());
    }

    /**
     * 处理上传文件超过限制的异常。
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("Upload exceeds max size: {}", e.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.fail(ResponseCode.FAIL.getCode(), "文件体积过大"));
    }

    /**
     * 处理请求方法不被当前接口支持的异常。
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResponse<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("Method not supported: {}", e.getMessage());
        return ApiResponse.fail(ResponseCode.FAIL.getCode(), "请求方法错误");
    }

    /**
     * 处理数据库唯一键冲突等重复数据异常。
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResponse<Void> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error("Duplicate key", e);
        return ApiResponse.fail(ResponseCode.FAIL.getCode(), "数据已存在");
    }

    /**
     * 处理通用数据库访问异常。
     */
    @ExceptionHandler(DataAccessException.class)
    public ApiResponse<Void> handleDataAccessException(DataAccessException e) {
        log.error("Database access error", e);
        return ApiResponse.fail(ResponseCode.SERVER_ERROR.getCode(), "数据库异常");
    }

    /**
     * 处理未被上面明确捕获的其他异常。
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("Unhandled exception", e);
        return ApiResponse.fail(ResponseCode.SERVER_ERROR.getCode(), ResponseCode.SERVER_ERROR.getMessage());
    }
}

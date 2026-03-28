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
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(this::buildFriendlyMessage)
                .findFirst()
                .orElse("参数验证失败");
        log.warn("Validation failed: {}", msg);
        return ApiResponse.fail(ResponseCode.FAIL.getCode(), msg);
    }

    private String buildFriendlyMessage(FieldError error) {
        String field = error.getField();
        String defaultMessage = error.getDefaultMessage();

        // 如果默认消息已经是友好的中文消息且不为空，直接返回
        if (defaultMessage != null && !defaultMessage.startsWith("{") && defaultMessage.length() < 50) {
            return defaultMessage;
        }

        // 从字段名推断中文名称
        String chineseFieldName = inferChineseFieldName(field);

        // 根据验证类型构建友好消息
        if (defaultMessage != null) {
            if (defaultMessage.contains("NotNull") || defaultMessage.contains("NotBlank") || defaultMessage.contains("NotEmpty")) {
                return chineseFieldName + "是必填的";
            }
            if (defaultMessage.contains("DecimalMax")) {
                return chineseFieldName + "不能大于" + extractLimit(defaultMessage, "max");
            }
            if (defaultMessage.contains("DecimalMin")) {
                return chineseFieldName + "不能小于" + extractLimit(defaultMessage, "min");
            }
            if (defaultMessage.contains("Max")) {
                return chineseFieldName + "不能大于" + extractLimit(defaultMessage, "max");
            }
            if (defaultMessage.contains("Min")) {
                return chineseFieldName + "不能小于" + extractLimit(defaultMessage, "min");
            }
            if (defaultMessage.contains("Size")) {
                return chineseFieldName + "长度不符合要求";
            }
            if (defaultMessage.contains("Pattern")) {
                return chineseFieldName + "格式不正确";
            }
            if (defaultMessage.contains("EnumValue")) {
                return chineseFieldName + "的值不在有效范围内";
            }
        }

        return chineseFieldName + "验证失败";
    }

    private String inferChineseFieldName(String field) {
        // 常见字段名映射
        Map<String, String> fieldNameMap = new HashMap<>();
        fieldNameMap.put("name", "名称");
        fieldNameMap.put("username", "用户名");
        fieldNameMap.put("realName", "姓名");
        fieldNameMap.put("phone", "手机号");
        fieldNameMap.put("status", "状态");
        fieldNameMap.put("storeId", "门店ID");
        fieldNameMap.put("current", "当前页码");
        fieldNameMap.put("size", "每页条数");
        fieldNameMap.put("address", "地址");
        fieldNameMap.put("province", "省份");
        fieldNameMap.put("city", "城市");
        fieldNameMap.put("district", "区县");
        fieldNameMap.put("openTime", "营业时间");
        fieldNameMap.put("longitude", "经度");
        fieldNameMap.put("latitude", "纬度");
        fieldNameMap.put("userType", "用户类型");
        fieldNameMap.put("dataScope", "数据范围");
        fieldNameMap.put("remark", "备注");
        fieldNameMap.put("password", "密码");
        fieldNameMap.put("oldPassword", "原密码");
        fieldNameMap.put("newPassword", "新密码");
        fieldNameMap.put("roleId", "角色ID");
        fieldNameMap.put("permissionName", "权限名称");

        return fieldNameMap.getOrDefault(field, field);
    }

    private String extractLimit(String message, String type) {
        // 从 Hibernate 验证消息中提取限制值
        Pattern pattern = Pattern.compile(type + "=" + "([\\d.]+)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
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
     * 处理空指针异常，通常发生在参数为 null 但被直接使用时。
     */
    @ExceptionHandler(NullPointerException.class)
    public ApiResponse<Void> handleNullPointerException(NullPointerException e) {
        String msg = "参数不能为空";
        // 尝试从异常堆栈中提取字段名
        String fieldName = extractFieldNameFromNPE(e);
        if (fieldName != null) {
            msg = inferChineseFieldName(fieldName) + "不能为空";
        }
        log.warn("NullPointerException: {}", e.getMessage());
        return ApiResponse.fail(ResponseCode.FAIL.getCode(), msg);
    }

    /**
     * 从 NullPointerException 中提取字段名
     */
    private String extractFieldNameFromNPE(NullPointerException e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length > 0) {
            for (StackTraceElement element : stackTrace) {
                String className = element.getClassName();
                String methodName = element.getMethodName();
                // 只检查业务代码
                if (className.startsWith("cn.dextea.")) {
                    // 尝试从 getter 方法名推断字段名
                    if (methodName.startsWith("get")) {
                        return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                    }
                }
            }
        }
        return null;
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

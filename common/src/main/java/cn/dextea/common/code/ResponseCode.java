package cn.dextea.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lai Yongchao
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS(200, "success"),
    FAIL(400, "失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "未找到资源"),
    SERVER_ERROR(500, "服务器内部错误");

    private final int code;
    private final String message;
}

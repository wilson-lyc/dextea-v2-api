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
    FAIL(400, "Fail"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    SERVER_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;
}

package cn.dextea.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    // 成功
    SUCCESS(200, "成功"),
    // 客户端错误
    BAD_REQUEST(400, "请求错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "未找到资源"),
    // 服务器错误
    SERVER_ERROR(500, "服务器内部错误");

    private final int code;
    private final String msg;
}

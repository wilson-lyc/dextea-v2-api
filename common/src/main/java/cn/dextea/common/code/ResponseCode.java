package cn.dextea.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用响应码，基于 HTTP 状态码设计。
 * 用于表示请求的整体结果状态（成功或失败类型）。
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS(0, "操作成功"),

    FAIL(400, "操作失败"),

    UNAUTHORIZED(401, "未登录或登录已过期"),

    FORBIDDEN(403, "无权访问"),

    NOT_FOUND(404, "资源不存在"),

    SERVER_ERROR(500, "服务器内部错误"),

    SERVICE_UNAVAILABLE(503, "服务暂不可用");

    private final Integer code;
    private final String message;
}

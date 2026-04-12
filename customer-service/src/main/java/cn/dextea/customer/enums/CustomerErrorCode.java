package cn.dextea.customer.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 顾客模块业务错误码。
 */
@Getter
@RequiredArgsConstructor
public enum CustomerErrorCode {
    EMAIL_ALREADY_EXISTS(40001, "邮箱已被注册"),
    REGISTER_FAILED(40002, "注册失败"),
    CUSTOMER_NOT_FOUND(40003, "顾客不存在"),
    LOGIN_FAILED(40004, "邮箱或密码错误"),
    ACCOUNT_DISABLED(40005, "账号已被禁用"),
    OLD_PASSWORD_INCORRECT(40006, "原密码错误"),
    UPDATE_PASSWORD_FAILED(40007, "修改密码失败"),
    UPDATE_PROFILE_FAILED(40008, "更新资料失败"),
    ENABLE_FAILED(40009, "启用顾客账号失败"),
    DISABLE_FAILED(40010, "禁用顾客账号失败"),
    EMAIL_CODE_SEND_TOO_FREQUENT(40011, "验证码发送过于频繁，请稍后再试"),
    EMAIL_CODE_SEND_FAILED(40012, "验证码发送失败"),
    EMAIL_CODE_INVALID(40013, "验证码无效或已过期");

    private final Integer code;
    private final String msg;
}

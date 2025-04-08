package cn.dextea.order.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum OrderErrorCode {
    NOT_FOUND("order.not.found", "订单不存在"),
    FORBIDDEN_REFUND("order.forbidden.refund", "订单不可退款"),
    REFUND_FAIL("order.refund.fail", "退款失败"),
    PASSWORD_INVALID("order.password.invalid", "操作者密码错误");

    private final String code;
    private final String msg;
}

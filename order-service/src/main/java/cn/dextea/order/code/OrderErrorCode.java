package cn.dextea.order.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum OrderErrorCode {
    ORDER_NOT_FOUND("order.not.found", "订单不存在"),
    ORDER_PAY_NOT_PENDING("order.pay.not.pending","订单非待支付，禁止支付"),
    ORDER_PAY_FAIL("order.pay.fail", "支付失败"),
    ORDER_REFUND_FORBIDDEN("order.refund.forbidden", "订单不可退款"),
    ORDER_REFUND_FAIL("order.refund.fail", "退款失败"),
    OPERATOR_PASSWORD_ILLEGAL("order.operator.password.illegal", "操作者密码错误"),
    ORDER_STATUS_NOT_MAKING("order.status.not.making", "订单状态不是待制作"),
    ORDER_PICK_UP_CALL_FORBIDDEN("order.pick.up.call.forbidden", "订单不可叫号");

    private final String code;
    private final String msg;
}

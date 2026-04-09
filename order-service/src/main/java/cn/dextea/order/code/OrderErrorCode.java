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
    ORDER_PAY_FAIL("order.pay.fail", "支付失败"),
    ORDER_PAY_DONE_FORBIDDEN("order.pay.done.forbidden","此订单不可支付"),
    ORDER_CANCEL_FORBIDDEN("order.cancel.forbidden", "此订单不可取消"),
    // 取餐叫号
    ORDER_CALL_FORBIDDEN("order.call.forbidden", "订单当前状态不可叫号"),
    // 更新订单状态相关
    ORDER_STATUS_WAIT_PICK_FORBIDDEN("order.status.wait.pick.forbidden", "此订单不可转为待取餐"),
    ORDER_STATUS_DONE_FORBIDDEN("order.status.done.forbidden", "此订单不可转为已完成"),
    ORDER_STATUS_REFUND_PASSWORD_ILLEGAL("order.status.refund.password.illegal", "密码错误"),
    ORDER_STATUS_REFUND_FORBIDDEN("order.status.refund.forbidden", "此订单不可退款"),
    ORDER_STATUS_REFUND_FAIL("order.status.refund.fail", "退款失败");

    private final String code;
    private final String msg;
}

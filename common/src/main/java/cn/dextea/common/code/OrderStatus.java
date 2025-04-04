package cn.dextea.common.code;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PAY_PENDING(0,"待支付"),
    MAKING(1,"制作中"),
    WAIT_PICK(2,"待取餐"),
    DONE(3,"已完成"),
    PAY_TIMEOUT(90,"支付超时"),
    CANCEL(91,"订单取消"),
    REFUND(92,"退款");

    @EnumValue
    private final int value;
    private final String label;

    public static OrderStatus fromValue(int value) {
        for (OrderStatus item : OrderStatus.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        throw new IllegalArgumentException("未知的订单状态码");
    }
}

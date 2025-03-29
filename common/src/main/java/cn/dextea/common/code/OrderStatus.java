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
    PAY_DONE(1,"已支付"),
    PAY_TIMEOUT(2, "支付超时"),
    MAKING(3,"制作中"),
    WAIT_PICK(4,"待取餐"),
    DONE(5,"已完成"),
    CANCEL(9,"订单取消");

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

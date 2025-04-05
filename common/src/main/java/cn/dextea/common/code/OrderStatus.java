package cn.dextea.common.code;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.Objects;

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
    PAY_TIMEOUT(90,"交易关闭"),
    CANCEL(91,"订单取消"),
    REFUND(92,"订单退款");

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

    public static OrderStatus fromValue(int value,String payExpireTime){
        if(Objects.isNull(payExpireTime)||value!=PAY_PENDING.getValue()){
            return OrderStatus.fromValue(value);
        }else{
            Date payExpireDate = DateUtil.parse(payExpireTime);
            Date nowDate = DateUtil.date();
            if(nowDate.after(payExpireDate)){
                return PAY_TIMEOUT;
            }else{
                return OrderStatus.fromValue(value);
            }
        }
    }
}

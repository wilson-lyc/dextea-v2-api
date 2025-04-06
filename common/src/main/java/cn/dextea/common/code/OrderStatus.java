package cn.dextea.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    UNKNOWN(-1,"未知"),
    PAY_PENDING(0,"待支付"),
    MAKING(1,"制作中"),
    WAIT_PICK(2,"待取餐"),
    DONE(3,"已完成"),
    PAY_TIMEOUT(90,"交易关闭"),
    CANCEL(91,"已取消"),
    REFUND(92,"已退款");

    private final int value;
    private final String label;

    public static OrderStatus fromValue(int value) {
        for (OrderStatus item : OrderStatus.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return UNKNOWN;
    }

    public static OrderStatus fromValue(int value,String time){
        // 非待支付
        if(value!=PAY_PENDING.getValue()||Objects.isNull(time)){
            return OrderStatus.fromValue(value);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime expireTime = LocalDateTime.parse(time, formatter);
        // 判断超时
        if(expireTime.isBefore(LocalDateTime.now())){
            return PAY_TIMEOUT;
        }else{
            return OrderStatus.fromValue(value);
        }
    }
}

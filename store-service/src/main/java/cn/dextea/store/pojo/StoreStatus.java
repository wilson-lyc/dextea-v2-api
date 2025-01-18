package cn.dextea.store.pojo;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lai Yongchao
 */
@Getter
@AllArgsConstructor
public enum StoreStatus {
    NOT_ACTIVE(0, "未激活"),
    CLOSE(1,"休息"),
    OPEN(2,"营业"),
    PAUSE_ORDER(3, "暂停点餐"),
    DELETE(4, "已注销");

    @EnumValue
    private final int code;
    private final String msg;
}

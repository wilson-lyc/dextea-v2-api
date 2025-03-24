package cn.dextea.common.code;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StoreStatus {
    NOT_ACTIVE(0,"未激活"),
    OPEN(1,"营业"),
    CLOSE(2, "闭店"),
    BUSY(3, "繁忙"),
    DELETE(9, "删除");

    @EnumValue
    private final int value;
    private final String label;

    public static StoreStatus fromValue(int value) {
        for (StoreStatus item : StoreStatus.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        throw new IllegalArgumentException("未知的门店状态码");
    }
}

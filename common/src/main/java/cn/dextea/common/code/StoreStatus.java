package cn.dextea.common.code;

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
    UNKNOWN(-1,"未知"),
    NOT_ACTIVE(0,"筹备中"),
    OPEN(1,"营业中"),
    CLOSE(2, "休息中"),
    BUSY(3, "门店繁忙"),
    DELETE(9, "已注销");

    private final int value;
    private final String label;

    public static StoreStatus fromValue(int value) {
        for (StoreStatus item : StoreStatus.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return UNKNOWN;
    }
}

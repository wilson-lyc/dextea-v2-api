package cn.dextea.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum DineMode {
    UNKNOWN(-1,"未知"),
    DINE_IN(0,"堂食"),
    TO_GO(1,"外带");

    private final int value;
    private final String label;

    public static DineMode fromValue(int value) {
        for (DineMode item : DineMode.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return UNKNOWN;
    }
}

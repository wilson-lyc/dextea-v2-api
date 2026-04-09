package cn.dextea.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum CustomizeItemStatus {
    UNKNOWN(-1,"未知"),
    FORBIDDEN(0,"禁用"),
    AVAILABLE(1,"可用");

    private final int value;
    private final String label;

    public static CustomizeItemStatus fromValue(int value) {
        for (CustomizeItemStatus item : CustomizeItemStatus.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return UNKNOWN;
    }
}

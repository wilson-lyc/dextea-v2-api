package cn.dextea.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum StaffStatus {
    UNKNOWN(-1,"未知"),
    FORBIDDEN(0,"禁用"),
    ACTIVE(1, "激活");

    private final int value;
    private final String label;

    public static StaffStatus fromValue(int value) {
        for (StaffStatus item : StaffStatus.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return UNKNOWN;
    }
}

package cn.dextea.staff.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum StaffStatus {
    DISABLED(0,"禁用"),
    ACTIVE(1, "启用"),
    INACTIVE(2, "未激活");

    private final int value;
    private final String label;

    public static boolean isValid(Integer value) {
        if (value == null) {
            return false;
        }
        for (StaffStatus status : values()) {
            if (status.value == value) {
                return true;
            }
        }
        return false;
    }
}

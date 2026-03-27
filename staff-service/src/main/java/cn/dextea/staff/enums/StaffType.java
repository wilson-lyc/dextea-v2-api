package cn.dextea.staff.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 员工类型枚举
 */
@Getter
@RequiredArgsConstructor
public enum StaffType {
    COMPANY(0, "公司员工"),
    STORE(1, "门店员工");

    private final int value;
    private final String label;

    public static boolean isValid(Integer value) {
        if (value == null) {
            return false;
        }
        for (StaffType type : values()) {
            if (type.value == value) {
                return true;
            }
        }
        return false;
    }
}

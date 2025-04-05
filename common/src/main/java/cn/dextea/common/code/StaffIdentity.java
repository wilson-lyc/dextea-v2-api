package cn.dextea.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum StaffIdentity {
    UNKNOWN(-1,"未知"),
    COMPANY(0, "公司员工"),
    STORE(1, "门店员工");

    private final int value;
    private final String label;

    public static StaffIdentity fromValue(int value) {
        for (StaffIdentity item : StaffIdentity.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return UNKNOWN;
    }
}
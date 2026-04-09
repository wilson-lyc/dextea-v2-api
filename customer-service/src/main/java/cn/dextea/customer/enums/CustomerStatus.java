package cn.dextea.customer.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomerStatus {
    DISABLED(0, "已禁用"),
    AVAILABLE(1, "正常");

    private final int value;
    private final String label;

    public static boolean isValid(Integer value) {
        if (value == null) {
            return false;
        }
        for (CustomerStatus status : values()) {
            if (status.value == value) {
                return true;
            }
        }
        return false;
    }
}

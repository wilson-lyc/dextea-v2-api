package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StorageMethod {
    REFRIGERATED(1, "冷藏"),
    FROZEN(2, "冷冻"),
    ROOM_TEMPERATURE(3, "常温");

    private final int value;
    private final String label;

    public static boolean isValid(Integer value) {
        if (value == null) {
            return false;
        }
        for (StorageMethod method : values()) {
            if (method.value == value) {
                return true;
            }
        }
        return false;
    }
}

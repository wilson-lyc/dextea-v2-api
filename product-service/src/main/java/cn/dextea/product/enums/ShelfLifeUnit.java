package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShelfLifeUnit {
    HOURS(1, "小时"),
    DAYS(2, "天");

    private final int value;
    private final String label;

    public static boolean isValid(Integer value) {
        if (value == null) {
            return false;
        }
        for (ShelfLifeUnit unit : values()) {
            if (unit.value == value) {
                return true;
            }
        }
        return false;
    }
}

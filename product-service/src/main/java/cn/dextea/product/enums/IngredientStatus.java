package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IngredientStatus {
    DISABLED(0, "禁用"),
    ACTIVE(1, "激活");

    private final int value;
    private final String label;

    public static boolean isValid(Integer value) {
        if (value == null) {
            return false;
        }
        for (IngredientStatus status : values()) {
            if (status.value == value) {
                return true;
            }
        }
        return false;
    }
}

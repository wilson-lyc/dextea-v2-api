package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {
    DISABLED(0, "下架"),
    ENABLED(1, "上架");

    private final int value;
    private final String label;

    public static boolean isValid(Integer value) {
        if (value == null) {
            return false;
        }
        for (ProductStatus status : values()) {
            if (status.value == value) {
                return true;
            }
        }
        return false;
    }
}

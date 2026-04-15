package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum StoreProductStatus {
    DISABLED(0, "售罄"),
    ENABLED(1, "在售");

    private final Integer value;
    private final String label;

    public static boolean isValid(Integer value) {
        if (value == null) {
            return false;
        }
        for (StoreProductStatus status : values()) {
            if (Objects.equals(status.value, value)) {
                return true;
            }
        }
        return false;
    }
}
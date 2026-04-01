package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreProductSaleStatus {
    SOLD_OUT(0, "售罄"),
    ON_SALE(1, "在售");

    private final int value;
    private final String label;

    public static boolean isValid(Integer value) {
        if (value == null) {
            return false;
        }
        for (StoreProductSaleStatus status : values()) {
            if (status.value == value) {
                return true;
            }
        }
        return false;
    }
}
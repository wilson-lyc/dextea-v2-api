package cn.dextea.store.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreStatus {
    REST(0, "休息"),
    OPEN(1, "营业"),
    BUSY(2, "繁忙"),
    CLOSED(3, "关店");

    private final int value;
    private final String label;

    public static boolean isValid(Integer value) {
        if (value == null) {
            return false;
        }
        for (StoreStatus status : values()) {
            if (status.value == value) {
                return true;
            }
        }
        return false;
    }
}

package cn.dextea.staff.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleDataScope {
    ALL(0, "所有数据"),
    BIND_STORE(1, "绑定门店数据"),
    CUSTOM(2, "自定义数据");

    private final int value;
    private final String label;

    public static boolean isValid(Integer value) {
        if (value == null) {
            return false;
        }
        for (RoleDataScope scope : values()) {
            if (scope.value == value) {
                return true;
            }
        }
        return false;
    }
}

package cn.dextea.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum CustomizeOptionStatus {
    UNKNOWN(-1,"未知"),
    GLOBAL_FORBIDDEN(0,"全局禁用"),
    AVAILABLE(1,"可用"),
    STORE_FORBIDDEN(2,"门店禁用");

    private final int value;
    private final String label;

    public static CustomizeOptionStatus fromValue(int value) {
        for (CustomizeOptionStatus item : CustomizeOptionStatus.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return UNKNOWN;
    }

    public static Integer getStatusValue(Integer globalStatus, Integer storeStatus) {
        if(globalStatus.equals(CustomizeOptionStatus.GLOBAL_FORBIDDEN.getValue())){
            return CustomizeOptionStatus.GLOBAL_FORBIDDEN.getValue();
        }else{
            return Objects.isNull(storeStatus) ? globalStatus : storeStatus;
        }
    }
}

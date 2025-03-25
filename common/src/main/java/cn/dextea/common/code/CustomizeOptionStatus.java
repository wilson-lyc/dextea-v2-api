package cn.dextea.common.code;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomizeOptionStatus {
    GLOBAL_FORBIDDEN(0,"全局禁用"),
    AVAILABLE(1,"可用"),
    STORE_FORBIDDEN(2,"门店禁用");

    @EnumValue
    private final int value;
    private final String label;

    public static CustomizeOptionStatus fromValue(int value) {
        for (CustomizeOptionStatus item : CustomizeOptionStatus.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        throw new IllegalArgumentException("未知的客制化选项状态码");
    }

    public static CustomizeOptionStatus getStatus(CustomizeOptionStatus globalStafus, CustomizeOptionStatus storeStatus) {
        if(globalStafus.equals(CustomizeOptionStatus.GLOBAL_FORBIDDEN)){
            return CustomizeOptionStatus.GLOBAL_FORBIDDEN;
        }else{
            return Objects.isNull(storeStatus)?globalStafus:storeStatus;
        }
    }
}

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

    public static Integer getStatus(Integer globalStatus, Integer storeStatus) {
        if(globalStatus.equals(CustomizeOptionStatus.GLOBAL_FORBIDDEN.getValue())){
            return CustomizeOptionStatus.GLOBAL_FORBIDDEN.getValue();
        }else{
            return Objects.isNull(storeStatus)?globalStatus:storeStatus;
        }
    }
}

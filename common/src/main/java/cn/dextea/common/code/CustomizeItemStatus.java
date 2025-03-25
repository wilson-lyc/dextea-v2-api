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
public enum CustomizeItemStatus {
    FORBIDDEN(0,"禁用"),
    AVAILABLE(1,"可用");

    @EnumValue
    private final int value;
    private final String label;
}

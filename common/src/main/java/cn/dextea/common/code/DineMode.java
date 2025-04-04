package cn.dextea.common.code;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DineMode {
    DINE_IN(0,"堂食"),
    TO_GO(1,"外带");

    @EnumValue
    private final int value;
    private final String label;

    public static DineMode fromValue(int value) {
        for (DineMode item : DineMode.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        throw new IllegalArgumentException("未知的用餐方式");
    }
}

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
public enum StaffStatus {
    FORBIDDEN(0,"禁用"),
    ACTIVE(1, "激活");

    @EnumValue
    private final int value;
    private final String label;

    public static StaffStatus fromValue(int value) {
        for (StaffStatus item : StaffStatus.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        throw new IllegalArgumentException("未知的员工状态码");
    }
}

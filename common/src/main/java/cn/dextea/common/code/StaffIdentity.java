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
public enum StaffIdentity {
    COMPANY(0, "公司"),
    STORE(1, "门店");

    @EnumValue
    private final int value;
    private final String label;

    public static StaffIdentity fromCode(int value) {
        for (StaffIdentity identity : StaffIdentity.values()) {
            if (identity.getValue() == value) {
                return identity;
            }
        }
        throw new IllegalArgumentException("未知的员工身份码");
    }
}
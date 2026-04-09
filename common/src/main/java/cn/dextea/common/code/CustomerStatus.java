package cn.dextea.common.code;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomerStatus {
    UNKNOWN(-1,"未知"),
    FORBIDDEN(0,"禁用"),
    ACTIVE(1,"激活");

    private final int value;
    private final String label;

    public static CustomerStatus fromValue(int value) {
        for (CustomerStatus item : CustomerStatus.values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return UNKNOWN;
    }
}

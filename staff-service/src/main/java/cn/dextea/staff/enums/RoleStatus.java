package cn.dextea.staff.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum RoleStatus {
    DISABLED(0,"禁用"),
    AVAILABLE(1, "可用");

    private final int value;
    private final String label;
}

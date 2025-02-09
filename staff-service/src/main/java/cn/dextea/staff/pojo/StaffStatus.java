package cn.dextea.staff.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StaffStatus {
    Disable(0, "禁用"),
    Enable(1, "启用");

    private final int code;
    private final String msg;
}

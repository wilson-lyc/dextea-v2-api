package cn.dextea.staff.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionErrorCode {
    PERMISSION_NOT_FOUND(12001, "权限不存在");

    private final Integer code;
    private final String msg;
}

package cn.dextea.staff.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleErrorCode {
    ROLE_NAME_ALREADY_EXISTS(11001, "角色名称已存在"),
    CREATE_FAILED(11002, "角色创建失败"),
    ROLE_NOT_FOUND(11003, "角色不存在"),
    UPDATE_FAILED(11004, "角色更新失败"),
    PERMISSION_NOT_FOUND(11005, "权限不存在"),
    ROLE_PERMISSION_ALREADY_BOUND(11006, "角色已绑定该权限"),
    ROLE_PERMISSION_REL_NOT_FOUND(11007, "角色未绑定该权限"),
    BIND_PERMISSION_FAILED(11008, "绑定权限失败"),
    UNBIND_PERMISSION_FAILED(11009, "解绑权限失败");

    private final Integer code;
    private final String msg;
}

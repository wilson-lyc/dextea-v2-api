package cn.dextea.auth.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum AuthErrorCode {
    ROLE_NAME_ILLEGAL("auth.role.name.illegal", "角色名非法，只能包含大小写字母、数字和下划线"),
    ROLE_CREATE_FAILED("auth.role.create.failed","角色创建失败"),
    ROLE_NOT_FOUND("auth.role.not.found", "角色不存在"),
    STAFF_NOT_FOUND("auth.staff.not.found", "员工不存在"),
    STAFF_BIND_ROLE_EXISTED("auth.staff.bind.role.existed", "员工已绑定此角色"),
    STAFF_BIND_ROLE_FAILED("auth.staff.bind.role.failed", "员工绑定角色失败"),
    STAFF_BIND_ROLE_NOT_EXISTED("auth.staff.bind.role.not.existed", "员工未绑定此角色"),
    STAFF_UNBIND_ROLE_FAILED("auth.staff.unbind.role.failed", "员工解绑角色失败");

    private final String code;
    private final String msg;
}

package cn.dextea.staff.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 员工模块业务错误码。
 */
@Getter
@RequiredArgsConstructor
public enum StaffErrorCode {
    USERNAME_ALREADY_EXISTS(10001, "用户名已存在"),
    CREATE_FAILED(10002, "员工创建失败"),
    STAFF_NOT_FOUND(10003, "员工不存在"),
    UPDATE_FAILED(10004, "员工更新失败"),
    LOGIN_FAILED(10005, "用户名或密码错误"),
    ACCOUNT_DISABLED(10006, "账号已被禁用"),
    RESET_PASSWORD_FAILED(10007, "重置密码失败"),
    ROLE_NOT_FOUND(10008, "角色不存在"),
    STAFF_ROLE_ALREADY_BOUND(10009, "员工已绑定该角色"),
    STAFF_ROLE_REL_NOT_FOUND(10010, "员工未绑定该角色"),
    ASSIGN_ROLE_FAILED(10011, "分配角色失败"),
    UNBIND_ROLE_FAILED(10012, "解绑角色失败"),
    OLD_PASSWORD_INCORRECT(10013, "原密码错误"),
    UPDATE_PASSWORD_FAILED(10014, "修改密码失败"),
    INVALID_USER_TYPE(10015, "员工类型错误"),
    INVALID_STATUS(10016, "员工状态错误"),
    STORE_NOT_FOUND(10017, "门店不存在"),
    STORE_BIND_ONLY_FOR_STORE_STAFF(10018, "仅门店员工支持绑定门店"),
    STAFF_STORE_ALREADY_BOUND(10019, "员工已绑定该门店"),
    STAFF_STORE_REL_NOT_FOUND(10020, "员工未绑定该门店"),
    BIND_STORE_FAILED(10021, "绑定门店失败"),
    UNBIND_STORE_FAILED(10022, "解绑门店失败");

    private final Integer code;
    private final String msg;
}

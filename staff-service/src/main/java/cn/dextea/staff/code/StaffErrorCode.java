package cn.dextea.staff.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum StaffErrorCode {
    STORE_ID_NULL("staff.store.id.null", "门店ID不可为空"),
    STORE_ID_ILLEGAL("staff.store.id.illegal", "门店ID错误"),
    CREATE_ACCOUNT_FAIL("staff.create.account.fail", "创建账号失败"),
    NOT_FOUND("staff.not.found", "员工不存在"),
    OLD_PASSWORD_ILLEGAL("staff.old.password.illegal", "旧密码错误"),
    LOGIN_FAIL("staff.login.fail", "账号或密码错误"),
    ACCOUNT_FORBIDDEN("staff.account.forbidden", "账号已被禁用");

    private final String code;
    private final String msg;
}

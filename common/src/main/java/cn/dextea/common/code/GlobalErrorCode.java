package cn.dextea.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode {
    PERMISSION_LACK("global.permission.lack", "您没有操作权限");
    private final String code;
    private final String msg;
}

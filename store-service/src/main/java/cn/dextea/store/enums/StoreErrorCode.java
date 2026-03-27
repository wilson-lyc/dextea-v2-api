package cn.dextea.store.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode {
    CREATE_FAILED(20001, "门店创建失败"),
    STORE_NOT_FOUND(20002, "门店不存在"),
    UPDATE_FAILED(20003, "门店更新失败"),
    DELETE_FAILED(20004, "门店删除失败"),
    INVALID_STATUS(20005, "门店状态错误"),
    GEOCODE_FAILED(20006, "门店地址解析失败");

    private final Integer code;
    private final String msg;
}

package cn.dextea.store.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum StoreErrorCode {
    STORE_STATUS_ERROR("store.status.error", "门店状态码错误"),
    STORE_NOT_FOUND("store.not.found", "门店不存在"),
    STORE_CUSTOMER_SEARCH_NULL("store.customer.search.null", "未找到相关门店");
    private final String code;
    private final String msg;
}

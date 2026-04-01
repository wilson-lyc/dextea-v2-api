package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 门店商品状态模块业务错误码。
 */
@Getter
@RequiredArgsConstructor
public enum StoreProductStatusErrorCode {
    RECORD_NOT_FOUND(31001, "门店商品状态记录不存在"),
    CREATE_FAILED(31002, "门店商品状态创建失败"),
    UPDATE_FAILED(31003, "门店商品状态更新失败"),
    STORE_PRODUCT_ALREADY_EXISTS(31004, "该门店商品状态记录已存在");

    private final Integer code;
    private final String msg;
}
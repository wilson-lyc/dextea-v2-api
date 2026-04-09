package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 商品模块业务错误码。
 */
@Getter
@RequiredArgsConstructor
public enum ProductErrorCode {
    PRODUCT_NOT_FOUND(30001, "商品不存在"),
    CREATE_FAILED(30002, "商品创建失败"),
    UPDATE_FAILED(30003, "商品更新失败"),
    NAME_ALREADY_EXISTS(30004, "商品名称已存在"),
    DELETE_FAILED(30005, "商品下架失败"),
    PRODUCT_NOT_AVAILABLE(30006, "商品暂不可售"),
    STORE_SALE_STATUS_UPDATE_FAILED(30007, "门店商品销售状态更新失败"),
    PRODUCT_DISABLED(30008, "商品已下架");

    private final Integer code;
    private final String msg;
}

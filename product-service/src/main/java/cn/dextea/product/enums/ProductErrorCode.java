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
    PRODUCT_NOT_AVAILABLE(30006, "商品暂不可售");

    private final Integer code;
    private final String msg;
}

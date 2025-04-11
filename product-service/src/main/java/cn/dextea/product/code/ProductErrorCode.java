package cn.dextea.product.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum ProductErrorCode {
    CATEGORY_NOT_FOUND("category.not.found", "商品分类不存在"),
    PRODUCT_EDIT_CATEGORY_ID_ERROR("product.edit.category.id.error", "商品分类ID错误"),
    PRODUCT_STATUS_STORE_ID_ERROR("product.status.store.id.error","门店ID错误"),
    PRODUCT_NOT_FOUND("product.not.found", "商品不存在"),
    PRODUCT_STATUS_GLOBAL_STATUS_ERROR("product.status.global.status.error","全局状态错误"),
    PRODUCT_STATUS_STORE_STATUS_ERROR("product.status.store.status.error","门店状态错误");
    private final String code;
    private final String msg;
}

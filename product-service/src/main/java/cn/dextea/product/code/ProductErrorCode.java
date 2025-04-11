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
    PRODUCT_NOT_FOUND("product.not.found", "商品不存在"),
    CUSTOMIZE_ITEM_NOT_FOUND("customize.item.not.found", "客制化项目不存在"),
    CUSTOMIZE_OPTION_NOT_FOUND("customize.option.not.found", "客制化选项不存在"),
    CATEGORY_ID_ERROR("category.id.error", "商品分类ID错误"),
    STORE_ID_ERROR("store.id.error","门店ID错误"),
    PRODUCT_ID_ERROR("product.id.error","商品ID错误"),
    CUSTOMIZE_ITEM_ID_ERROR("customize.item.id.error","客制化项目ID错误"),
    GLOBAL_STATUS_ERROR("global.status.error","全局状态错误"),
    STORE_STATUS_ERROR("store.status.error","门店状态错误");
    private final String code;
    private final String msg;
}

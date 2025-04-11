package cn.dextea.menu.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum MenuErrorCode {
    MENU_NOT_FOUND("menu.not.found", "菜单不存在"),
    MENU_ID_ILLEGAL("menu.id.illegal", "菜单id不合法"),
    GROUP_NOT_FOUND("group.not.found", "分组不存在"),
    GROUP_ID_ILLEGAL("group.id.illegal", "分组id不合法"),
    PRODUCT_ID_ILLEGAL("product.id.illegal", "商品id不合法"),
    PRODUCT_EXISTED("product.existed","商品已存在"),
    PRODUCT_MISSED("product.missed","商品不存在");

    private final String code;
    private final String msg;
}

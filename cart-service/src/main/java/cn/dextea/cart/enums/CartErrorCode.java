package cn.dextea.cart.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode {
    PRODUCT_NOT_FOUND(50001, "商品不存在或已下架"),
    INVALID_OPTION(50002, "客制化选项无效"),
    CART_ITEM_NOT_FOUND(50003, "购物车条目不存在");

    private final Integer code;
    private final String msg;
}

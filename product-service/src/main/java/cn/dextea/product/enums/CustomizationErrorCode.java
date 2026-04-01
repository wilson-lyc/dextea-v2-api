package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomizationErrorCode {
    ITEM_NOT_FOUND(34001, "定制项不存在"),
    ITEM_NAME_DUPLICATE(34002, "该产品下已存在同名定制项"),
    OPTION_NOT_FOUND(34003, "定制选项不存在"),
    OPTION_NAME_DUPLICATE(34004, "该定制项下已存在同名选项"),
    INGREDIENT_BINDING_NOT_FOUND(34005, "原料绑定不存在"),
    PRODUCT_NOT_FOUND(34006, "产品不存在"),
    INGREDIENT_NOT_FOUND(34007, "原料不存在"),
    DEFAULT_OPTION_CONFLICT(34008, "同一定制项只能有一个默认选项");

    private final Integer code;
    private final String msg;
}

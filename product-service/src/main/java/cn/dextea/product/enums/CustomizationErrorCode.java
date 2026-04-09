package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomizationErrorCode {
    ITEM_NOT_FOUND(34001, "客制化项目不存在"),
    ITEM_NAME_DUPLICATE(34002, "该名称的客制化项目已存在"),
    ITEM_DELETE_FAILED(34003, "客制化项目删除失败"),
    OPTION_NOT_FOUND(34004, "客制化选项不存在"),
    OPTION_NAME_DUPLICATE(34005, "该项目下已存在同名客制化选项"),
    OPTION_DELETE_FAILED(34006, "客制化选项删除失败"),
    INGREDIENT_NOT_FOUND(34007, "原料不存在"),
    STORE_ITEM_SALE_STATUS_UPDATE_FAILED(34008, "门店客制化项目在售状态更新失败"),
    STORE_OPTION_SALE_STATUS_UPDATE_FAILED(34009, "门店客制化选项在售状态更新失败");

    private final Integer code;
    private final String msg;
}

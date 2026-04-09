package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IngredientErrorCode {
    INGREDIENT_NOT_FOUND(31001, "原料不存在"),
    CREATE_FAILED(31002, "原料创建失败"),
    UPDATE_FAILED(31003, "原料更新失败"),
    NAME_ALREADY_EXISTS(31004, "原料名称已存在"),
    INGREDIENT_ALREADY_BOUND(31005, "原料已绑定至该商品"),
    INGREDIENT_NOT_BOUND(31006, "原料未绑定至该商品"),
    INVENTORY_UPDATE_FAILED(31007, "库存更新失败");

    private final Integer code;
    private final String msg;
}

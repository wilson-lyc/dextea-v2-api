package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InventoryErrorCode {
    INVENTORY_NOT_FOUND(32001, "库存记录不存在"),
    STORE_NOT_FOUND(32002, "门店不存在或已关闭"),
    INGREDIENT_NOT_FOUND(32003, "原料不存在"),
    SET_FAILED(32004, "库存设置失败"),
    ADJUST_FAILED(32005, "库存调整失败"),
    INSUFFICIENT_STOCK(32006, "库存不足，调整后数量不能为负");

    private final Integer code;
    private final String msg;
}

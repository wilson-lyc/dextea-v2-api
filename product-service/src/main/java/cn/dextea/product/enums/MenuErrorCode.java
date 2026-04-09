package cn.dextea.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MenuErrorCode {
    MENU_NOT_FOUND(35001, "菜单不存在"),
    MENU_NAME_ALREADY_EXISTS(35002, "菜单名称已存在"),
    CREATE_FAILED(35003, "菜单创建失败"),
    UPDATE_FAILED(35004, "菜单更新失败"),
    DELETE_FAILED(35005, "菜单删除失败"),
    MENU_HAS_BOUND_STORES(35006, "菜单已绑定门店，无法删除"),
    STORE_ALREADY_HAS_MENU(35007, "该门店已绑定菜单"),
    STORE_MENU_BINDING_NOT_FOUND(35008, "门店菜单绑定关系不存在"),
    STORE_MENU_NOT_BOUND(35009, "该门店未绑定菜单");

    private final Integer code;
    private final String msg;
}

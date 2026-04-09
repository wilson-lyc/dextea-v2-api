package cn.dextea.order.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Getter
@RequiredArgsConstructor
public enum WSMsgType {
    PING(0, "ping"),
    PONG(1, "pong"),
    CONNECT_SUCCESS(2, "连接成功"),
    NEW_ORDER(3, "新订单"),
    CALL_AND_ADD_LIST(4, "取餐叫号并加入列表"),
    CALL_ONLY(5, "只取餐叫号"),
    ORDER_LIST(6, "订单列表");

    private final int value;
    private final String label;
}

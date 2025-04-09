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
    CALL_PICK_UP(4, "取餐叫号"),
    CALL_ONLY(5, "只叫号");

    private final int value;
    private final String label;
}

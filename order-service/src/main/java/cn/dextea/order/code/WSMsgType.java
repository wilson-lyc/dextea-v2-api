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
    TEXT(2, "文本消息"),
    NEW_ORDER(3, "新订单");

    private final int value;
    private final String label;
}

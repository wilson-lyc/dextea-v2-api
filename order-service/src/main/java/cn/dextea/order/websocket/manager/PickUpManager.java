package cn.dextea.order.websocket.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Lai Yongchao
 * 订单推送服务 - session池
 */
@Slf4j
public class PickUpManager {
    private static final Map<Object, Set<WebSocketSession>> SESSION_POOL = new ConcurrentHashMap<>();

    /**
     * 保存session
     * @param storeId 门店ID
     * @param session 会话对象
     */
    public static void addSession(Object storeId, WebSocketSession session) {
        log.info("保存session storeId={} sessionId={}", storeId, session.getId());
        // 如果不存在，则创建一个空的集合
        SESSION_POOL.computeIfAbsent(storeId, key -> new CopyOnWriteArraySet<>()).add(session);
    }

    /**
     * 获取会话集合
     * @param storeId 门店ID
     */
    public static Set<WebSocketSession> getSession(Object storeId) {
        log.info("获取session storeId={}", storeId);
        // 若不存在，则返回一个空的集合
        return SESSION_POOL.getOrDefault(storeId, new CopyOnWriteArraySet<>());
    }

    /**
     * 移除会话
     * @param storeId 门店ID
     * @param session 会话对象
     */
    public static void removeSession(Object storeId, WebSocketSession session) {
        log.info("移除session storeId={} sessionId={}", storeId, session.getId());
        Set<WebSocketSession> sessions = SESSION_POOL.get(storeId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                SESSION_POOL.remove(storeId);
            }
        }
    }
}

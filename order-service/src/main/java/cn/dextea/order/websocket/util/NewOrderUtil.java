package cn.dextea.order.websocket.util;

import cn.dextea.order.model.WSMsgModel;
import cn.dextea.order.websocket.manager.NewOrderManager;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

/**
 * @author Lai Yongchao
 */
@Slf4j
@Component
public class NewOrderUtil {
    /**
     * 发送消息
     * @param id 门店ID
     * @param msg 消息
     */
    public void sendMsg(Long id, WSMsgModel msg){
        Set<WebSocketSession> sessions = NewOrderManager.getSession(String.valueOf(id));
        log.info("发送消息 StoreId={} SessionSize={}",id,sessions.size());
        sessions.forEach(session -> {
            try {
                log.info("发送消息 StoreId={} SessionId={} Message={}",id,session.getId(),msg);
                session.sendMessage(new TextMessage(JSONObject.toJSONString(msg)));
            } catch (Exception e) {
                log.error("websocket消息发送失败：{}", e.getMessage());
            }
        });
    }
}

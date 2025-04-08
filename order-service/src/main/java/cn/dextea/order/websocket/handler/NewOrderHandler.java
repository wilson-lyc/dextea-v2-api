package cn.dextea.order.websocket.handler;

import cn.dextea.order.websocket.manager.NewOrderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Lai Yongchao
 * 新订单推送服务
 */
@Slf4j
@Component
public class NewOrderHandler extends TextWebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        Object storeId=session.getAttributes().get("storeId");
        log.info("连接成功 storeId={} sessionId={}",storeId,session.getId());
        // 调整内容长度
        session.setTextMessageSizeLimit(Integer.MAX_VALUE);
        if(storeId!=null){
            NewOrderManager.addSession(storeId,session);
            try {
                session.sendMessage(new TextMessage("Hi! 欢迎光临德贤茶庄"));
            } catch (IOException e) {
                throw new RuntimeException("欢迎消息发送失败");
            }
        }else{
            log.error("缺少storeId，关闭连接 sessionId={}",session.getId());
            throw new RuntimeException("缺少storeId，关闭连接");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        Object storeId=session.getAttributes().get("storeId");
        log.info("断开连接 storeId={} sessionId={} CloseStatus={}",storeId,session.getId(),status);
        if(Objects.nonNull(storeId)) {
            NewOrderManager.removeSession(storeId, session);
        }else{
            throw new RuntimeException("缺少storeId，连接关闭失败");
        }
    }
}

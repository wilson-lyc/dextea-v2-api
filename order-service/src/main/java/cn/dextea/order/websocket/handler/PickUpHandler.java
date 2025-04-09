package cn.dextea.order.websocket.handler;

import cn.dextea.order.code.WSMsgType;
import cn.dextea.order.model.WSMsgModel;
import cn.dextea.order.websocket.manager.PickUpManager;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
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
public class PickUpHandler extends TextWebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        Object storeId=session.getAttributes().get("storeId");
        log.info("连接成功 storeId={} sessionId={}",storeId,session.getId());
        // 调整内容长度
        session.setTextMessageSizeLimit(512*1024);
        if(storeId!=null){
            PickUpManager.addSession(storeId,session);
            try {
                WSMsgModel msg=new WSMsgModel(WSMsgType.CONNECT_SUCCESS.getValue(),
                        JSONObject.of("msg","服务连接成功"));
                session.sendMessage(new TextMessage(JSONObject.toJSONString(msg)));
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
            PickUpManager.removeSession(storeId, session);
        }else{
            throw new RuntimeException("缺少storeId，连接关闭失败");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        WSMsgModel model = new WSMsgModel(WSMsgType.PONG.getValue(), null);
        session.sendMessage(new TextMessage(JSONObject.toJSONString(model)));
    }
}

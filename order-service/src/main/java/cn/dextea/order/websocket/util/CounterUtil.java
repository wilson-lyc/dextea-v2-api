package cn.dextea.order.websocket.util;

import cn.dextea.common.feign.OrderFeign;
import cn.dextea.common.model.order.CounterOrderListModel;
import cn.dextea.order.code.WSMsgType;
import cn.dextea.order.model.WSMsgModel;
import cn.dextea.order.util.AudioUtil;
import cn.dextea.order.websocket.manager.CounterManager;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
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
public class CounterUtil {
    @Resource
    private OrderFeign orderFeign;
    @Resource
    AudioUtil audioUtil;
    public void sendOrder(Long storeId){
        // 获取订单
        CounterOrderListModel orderList=orderFeign.getOrderForCounter(storeId);
        // 构建消息
        WSMsgModel msg=new WSMsgModel(WSMsgType.ORDER_LIST.getValue(),
                JSONObject.of("orderList",orderList));
        String msgJsonStr=JSONObject.toJSONString(msg);
        // 获取会话
        Set<WebSocketSession> sessions = CounterManager.getSession(String.valueOf(storeId));
        log.info("发送订单 StoreId={} SessionSize={}",storeId,sessions.size());
        sessions.forEach(session -> {
            try {
                log.info("发送订单 StoreId={} SessionId={}",storeId,session.getId());
                session.sendMessage(new TextMessage(msgJsonStr));
            } catch (Exception e) {
                log.error("订单发送失败：{}", e.getMessage());
            }
        });
    }

    public void callNewOrder(Long storeId){
        // 生成音频
        String audio=audioUtil.getAudioBase64(104);
        // 构建消息
        WSMsgModel msg=new WSMsgModel(WSMsgType.NEW_ORDER.getValue(),
                JSONObject.of("audio",audio));
        String msgJsonStr=JSONObject.toJSONString(msg);
        // 获取会话
        Set<WebSocketSession> sessions = CounterManager.getSession(String.valueOf(storeId));
        log.info("新订单提示 StoreId={} SessionSize={}",storeId,sessions.size());
        // 发送
        sessions.forEach(session -> {
            try {
                log.info("新订单提示 StoreId={} SessionId={}",storeId,session.getId());
                session.sendMessage(new TextMessage(msgJsonStr));
            } catch (Exception e) {
                log.error("新订单提示发送失败：{}", e.getMessage());
            }
        });
    }
}

package cn.dextea.order.websocket.util;

import cn.dextea.order.code.WSMsgType;
import cn.dextea.order.model.WSMsgModel;
import cn.dextea.order.util.AudioUtil;
import cn.dextea.order.websocket.manager.PickUpManager;
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
public class PickUpCallUtil {
    @Resource
    AudioUtil audioUtil;
    public void call(Long storeId,String pickUpNo){
        //拆分取餐号
        String[] noStr = pickUpNo.split("");
        // 生成音频
        String audio=audioUtil.getAudioBase64(101,102,
                Integer.valueOf(noStr[0]),
                Integer.valueOf(noStr[1]),
                Integer.valueOf(noStr[2]),
                Integer.valueOf(noStr[3]),
                103);
        WSMsgModel msg=new WSMsgModel(WSMsgType.CALL_PICK_UP.getValue(),
                JSONObject.of("pickUpNo",pickUpNo,"audio",audio));
        String msgJsonStr=JSONObject.toJSONString(msg);
        // 获取会话
        Set<WebSocketSession> sessions = PickUpManager.getSession(String.valueOf(storeId));
        log.info("取餐叫号 StoreId={} SessionSize={}",storeId,sessions.size());
        sessions.forEach(session -> {
            try {
                log.info("发送音频 StoreId={} SessionId={}",storeId,session.getId());
                session.sendMessage(new TextMessage(msgJsonStr));
            } catch (Exception e) {
                log.error("音频发送失败：{}", e.getMessage());
            }
        });
    }

    public void callOnly(Long storeId,String pickUpNo){
        //拆分取餐号
        String[] noStr = pickUpNo.split("");
        // 生成音频
        String audio=audioUtil.getAudioBase64(101,102,
                Integer.valueOf(noStr[0]),
                Integer.valueOf(noStr[1]),
                Integer.valueOf(noStr[2]),
                Integer.valueOf(noStr[3]),
                103);
        WSMsgModel msg=new WSMsgModel(WSMsgType.CALL_ONLY.getValue(),
                JSONObject.of("pickUpNo",pickUpNo,"audio",audio));
        String msgJsonStr=JSONObject.toJSONString(msg);
        // 获取会话
        Set<WebSocketSession> sessions = PickUpManager.getSession(String.valueOf(storeId));
        log.info("取餐叫号 StoreId={} SessionSize={}",storeId,sessions.size());
        sessions.forEach(session -> {
            try {
                log.info("发送音频 StoreId={} SessionId={}",storeId,session.getId());
                session.sendMessage(new TextMessage(msgJsonStr));
            } catch (Exception e) {
                log.error("音频发送失败：{}", e.getMessage());
            }
        });
    }
}

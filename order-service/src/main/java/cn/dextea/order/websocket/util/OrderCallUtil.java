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
public class OrderCallUtil {
    @Resource
    AudioUtil audioUtil;

    private String getAudio(String val){
        String[] str = val.split("");
        return audioUtil.getAudioBase64(101,102,
                Integer.valueOf(str[0]),
                Integer.valueOf(str[1]),
                Integer.valueOf(str[2]),
                Integer.valueOf(str[3]),
                103);
    }

    private void sendMsg(Long storeId, WSMsgModel msg){
        String msgJsonStr=JSONObject.toJSONString(msg);
        // 获取会话
        Set<WebSocketSession> sessions = PickUpManager.getSession(String.valueOf(storeId));
        log.info("取餐叫号 StoreId={} SessionSize={}",storeId,sessions.size());
        sessions.forEach(session -> {
            try {
                log.info("取餐叫号 StoreId={} SessionId={}",storeId,session.getId());
                session.sendMessage(new TextMessage(msgJsonStr));
            } catch (Exception e) {
                log.error("取餐叫号发送：{}", e.getMessage());
            }
        });
    }

    public void callAndAddList(Long storeId, String pickUpNo){
        // 生成音频
        String audio=getAudio(pickUpNo);
        // 生成消息
        WSMsgModel msg=new WSMsgModel(WSMsgType.CALL_AND_ADD_LIST.getValue(),
                JSONObject.of("pickUpNo",pickUpNo,"audio",audio));
        // 发送消息
        sendMsg(storeId,msg);
    }

    public void callOnly(Long storeId,String pickUpNo){
        // 生成音频
        String audio=getAudio(pickUpNo);
        // 生成消息
        WSMsgModel msg=new WSMsgModel(WSMsgType.CALL_ONLY.getValue(),
                JSONObject.of("pickUpNo",pickUpNo,"audio",audio));
        // 发送消息
        sendMsg(storeId,msg);
    }
}

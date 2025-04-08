package cn.dextea.order.websocket.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * @author Lai Yongchao
 */
@Slf4j
@Component
public class WebSocketInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("WebSocket开始握手");
        log.info(request.getURI().getHost());
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String storeId = servletRequest.getURI().getQuery();
            if (storeId != null && storeId.contains("storeId=")) {
                storeId = storeId.split("storeId=")[1];
                attributes.put("storeId", storeId);
                log.info("识别到 storeId={} 允许连接", storeId);
                return true;
            }
        }
        log.error("未识别到storeID，禁止连接");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("WebSocket握手完成");
    }
}

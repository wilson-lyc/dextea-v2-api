package cn.dextea.order.config;

import cn.dextea.order.websocket.handler.NewOrderHandler;
import cn.dextea.order.websocket.handler.PickUpHandler;
import cn.dextea.order.websocket.interceptor.WebSocketInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author Lai Yongchao
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Resource
    private NewOrderHandler newOrderHandler;
    @Resource
    private PickUpHandler pickUpHandler;
    @Resource
    private WebSocketInterceptor webSocketInterceptor;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(newOrderHandler,"/order/ws/newOrder")
                .addHandler(pickUpHandler,"/order/ws/pick-up")
                .addInterceptors(webSocketInterceptor)
                .setAllowedOriginPatterns("*");
    }
}

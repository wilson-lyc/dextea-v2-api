package cn.dextea.gateway.filter;

import cn.dextea.common.util.DexteaJWTUtil;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * @author Lai Yongchao
 */
@Slf4j
@Component
public class CustomerFilter extends AbstractGatewayFilterFactory<CustomerFilter.Config> implements Ordered {
    @Resource
    private DexteaJWTUtil jwtUtil;

    // 白名单
    private static final List<String> WHITE_LIST = Arrays.asList("/customer/login");

    public CustomerFilter() {
        super(Config.class);
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token=exchange.getRequest().getHeaders().getFirst("DexteaToken");
            String path=exchange.getRequest().getPath().value();
            // 白名单放行
            if (WHITE_LIST.contains(path)) {
                return chain.filter(exchange);
            }
            if(!jwtUtil.verifyToken(token)) {
                log.error("toke无效:{}",token);
                JSONObject res=JSONObject.of(
                        "code",401,
                        "message","token无效");
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                exchange.getResponse().getHeaders().add("Content-Type", "application/json");
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(res.toJSONString().getBytes());
                return exchange.getResponse().writeWith(Mono.just(buffer));
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
    }

    @Override
    public int getOrder() {
        return 2;
    }
}

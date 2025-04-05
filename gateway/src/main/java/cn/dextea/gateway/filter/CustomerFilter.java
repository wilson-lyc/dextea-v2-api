package cn.dextea.gateway.filter;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Slf4j
@Component
public class CustomerFilter extends AbstractGatewayFilterFactory<CustomerFilter.Config> implements Ordered {

    // 白名单
    private static final List<String> WHITE_LIST = Arrays.asList("/customer");

    public CustomerFilter() {
        super(Config.class);
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String token=exchange.getRequest().getHeaders().getFirst("Authorization");
            String path=exchange.getRequest().getPath().value();
            // 白名单放行
            if (isWhiteListPath(path)) {
                log.info("白名单放行 path={}",path);
                return chain.filter(exchange);
            }
            // 校验token合法性

            return chain.filter(exchange);
        };
    }

    public static class Config {
    }

    @Override
    public int getOrder() {
        return 1;
    }

    private boolean isWhiteListPath(String path) {
        return WHITE_LIST.stream()
                .anyMatch(whitePath -> path.matches(whitePath.replace("**", ".*")));
    }
}

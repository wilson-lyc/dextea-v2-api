package cn.dextea.gateway.filter;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 * 全局拦截器
 * 检查Header中是否存在token
 * 登录接口放行
 */
@Slf4j
@Component
public class DexteaGlobalFilter implements GlobalFilter, Ordered {

    // 白名单
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/staff/login",
            "/customer");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token=exchange.getRequest().getHeaders().getFirst("Authorization");
        String path=exchange.getRequest().getPath().value();
        log.info("path={}",path);
        // 白名单放行
        if (isWhiteListPath(path)) {
            log.info("白名单放行 path={}",path);
            return chain.filter(exchange);
        }
        // 校验token
        log.info("token={}",token);
        if(Objects.isNull(token)){
            log.error("Header缺少token");
            JSONObject res=JSONObject.of(
                    "code",401,
                    "message","请提供认证信息");
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().add("Content-Type", "application/json");
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(res.toJSONString().getBytes());
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private boolean isWhiteListPath(String path) {
        return WHITE_LIST.stream()
                .anyMatch(whitePath -> path.matches(whitePath.replace("**", ".*")));
    }
}

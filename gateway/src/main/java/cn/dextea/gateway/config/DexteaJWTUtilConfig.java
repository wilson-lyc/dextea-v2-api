package cn.dextea.gateway.config;

import cn.dextea.common.util.DexteaJWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lai Yongchao
 */
@Configuration
public class DexteaJWTUtilConfig {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Bean
    public DexteaJWTUtil jwtUtil() {
        return new DexteaJWTUtil(secretKey);
    }
}

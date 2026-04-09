package cn.dextea.store.config;

import cn.dextea.common.util.TosUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lai Yongchao
 */
@Configuration
public class TosUtilConfig {
    @Value("${tos.access_key}")
    private String ACCESS_KEY;
    @Value("${tos.secret_key}")
    private String SECRET_KEY;

    @Bean
    public TosUtil tosUtil() {
        return new TosUtil(ACCESS_KEY, SECRET_KEY);
    }
}

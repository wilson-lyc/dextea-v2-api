package cn.dextea.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lai Yongchao
 */
@Configuration
@RefreshScope
public class AlipayClientConfig {
    @Value("${alipay.serverUrl}")
    String SERVER_URL;
    @Value("${alipay.appId}")
    String APP_ID;
    @Value("${alipay.privateKey}")
    String PRIVATE_KEY;
    @Value("${alipay.alipayPublicKey}")
    String ALIPAY_PUBLIC_KEY;

    @Bean
    public AlipayClient alipayClient() throws AlipayApiException {
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl(SERVER_URL);
        alipayConfig.setAppId(APP_ID);
        alipayConfig.setPrivateKey(PRIVATE_KEY);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(ALIPAY_PUBLIC_KEY);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return new DefaultAlipayClient(alipayConfig);
    }
}
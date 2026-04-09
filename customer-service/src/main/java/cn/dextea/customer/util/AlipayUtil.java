package cn.dextea.customer.util;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author Lai Yongchao
 */
@Component
public class AlipayUtil {
    @Resource
    private AlipayClient alipayClient;
    public AlipaySystemOauthTokenResponse getSystemOauthToken(String authCode) throws AlipayApiException {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("authorization_code");
        request.setCode(authCode);
        AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            return response;
        }else {
            throw new AlipayApiException("授权失败:"+response.getSubMsg()+"("+response.getSubCode()+")");
        }
    }
}

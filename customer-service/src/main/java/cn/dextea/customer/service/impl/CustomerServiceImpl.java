package cn.dextea.customer.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.customer.dto.CustomerLoginDTO;
import cn.dextea.customer.mapper.CustomerMapper;
import cn.dextea.customer.service.CustomerService;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author Lai Yongchao
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private AlipayClient alipayClient;
    @Override
    public ApiResponse customerLogin(CustomerLoginDTO data) throws AlipayApiException {
        // 构造请求参数以调用接口
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        // 设置授权方式
        request.setGrantType("authorization_code");
        // 设置授权码
        request.setCode(data.getAuthCode());
        AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
        return ApiResponse.success(response.getOpenId());
    }
}

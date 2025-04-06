package cn.dextea.customer.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.customer.dto.CustomerListResponse;
import cn.dextea.customer.dto.CustomerLoginRequest;
import cn.dextea.customer.dto.CustomerLoginResponse;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @author Lai Yongchao
 */
public interface CustomerService {
    DexteaApiResponse<CustomerLoginResponse> customerLogin(CustomerLoginRequest data) throws AlipayApiException;
    DexteaApiResponse<IPage<CustomerListResponse>> getCustomerList(int current, int size);
}

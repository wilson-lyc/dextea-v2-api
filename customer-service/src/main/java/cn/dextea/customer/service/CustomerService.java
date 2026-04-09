package cn.dextea.customer.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.customer.CustomerModel;
import cn.dextea.customer.model.CustomerFilter;
import cn.dextea.customer.model.CustomerLoginRequest;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @author Lai Yongchao
 */
public interface CustomerService {
    DexteaApiResponse<CustomerModel> customerLogin(CustomerLoginRequest data) throws AlipayApiException;
    DexteaApiResponse<IPage<CustomerModel>> getCustomerList(int current, int size, CustomerFilter filter);
    DexteaApiResponse<CustomerModel> getCustomerDetail(Long id);
    DexteaApiResponse<Void> updateCustomerStatus(Long id, Integer status);
}

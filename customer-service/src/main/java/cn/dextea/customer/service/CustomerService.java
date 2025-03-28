package cn.dextea.customer.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.customer.dto.CustomerLoginDTO;
import com.alipay.api.AlipayApiException;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface CustomerService {
    ApiResponse customerLogin(CustomerLoginDTO data) throws AlipayApiException;
}

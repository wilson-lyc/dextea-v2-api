package cn.dextea.order.service;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.order.dto.PayCreateRequest;
import cn.dextea.order.dto.PayCreateResponse;
import com.alipay.api.AlipayApiException;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface PayService {
    DexteaApiResponse<PayCreateResponse> createPay(PayCreateRequest request) throws AlipayApiException;
}

package cn.dextea.order.controller;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.order.dto.PayCreateRequest;
import cn.dextea.order.dto.PayCreateResponse;
import cn.dextea.order.service.PayService;
import com.alipay.api.AlipayApiException;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lai Yongchao
 */
@RestController
public class PayController {
    @Resource
    private PayService payService;
    @PostMapping("/pay")
    public DexteaApiResponse<PayCreateResponse> createPay(@Valid @RequestBody PayCreateRequest request) throws AlipayApiException {
        return payService.createPay(request);
    }
}

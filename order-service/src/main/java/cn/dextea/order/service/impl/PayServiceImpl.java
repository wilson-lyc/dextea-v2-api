package cn.dextea.order.service.impl;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.order.dto.PayCreateRequest;
import cn.dextea.order.dto.PayCreateResponse;
import cn.dextea.order.service.PayService;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeCreateModel;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * @author Lai Yongchao
 */
@Service
@RefreshScope
public class PayServiceImpl implements PayService {
    @Resource
    private AlipayClient alipayClient;
    @Value("${alipay.appId}")
    private String APP_ID;
    @Override
    public DexteaApiResponse<PayCreateResponse> createPay(PayCreateRequest data) throws AlipayApiException {
        AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
        AlipayTradeCreateModel model = new AlipayTradeCreateModel();
        // 设置商户订单号
        model.setOutTradeNo(data.getOrderId());
        // 设置产品码
        model.setProductCode("JSAPI_PAY");
        // 设置小程序支付中
        model.setOpAppId(APP_ID);
        // 设置订单总金额
        model.setTotalAmount(String.valueOf(data.getTotalPrice()));
        // 设置订单标题
        model.setSubject(data.getSubject());
        // 设置买家支付宝用户唯一标识
        model.setBuyerOpenId(data.getBuyerOpenId());
        request.setBizModel(model);
        AlipayTradeCreateResponse response = alipayClient.execute(request);
        System.out.println(JSONObject.from(response));
        return DexteaApiResponse.success();
    }
}

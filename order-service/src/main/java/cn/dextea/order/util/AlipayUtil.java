package cn.dextea.order.util;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeCreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Lai Yongchao
 */
@Component
@RefreshScope
public class AlipayUtil {
    @Resource
    private AlipayClient alipayClient;
    @Value("${alipay.appId}")
    String APP_ID;

    public AlipayTradeCreateResponse tradeCreate(String orderId,String buyerOpenId, BigDecimal totalPrice) throws AlipayApiException {
        AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
        AlipayTradeCreateModel model = new AlipayTradeCreateModel();
        // 设置商户订单号
        model.setOutTradeNo(orderId);
        // 设置产品码
        model.setProductCode("JSAPI_PAY");
        // 设置小程序码
        model.setOpAppId(APP_ID);
        // 设置买家支付宝用户唯一标识
        model.setBuyerOpenId(buyerOpenId);
        // 设置订单总金额
        model.setTotalAmount(totalPrice.toString());
        model.setSubject("德贤茶庄");
        // 设置过期时间
        model.setTimeoutExpress("10m");
        request.setBizModel(model);
        AlipayTradeCreateResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            return response;
        }else {
            throw new RuntimeException("支付宝交易创建失败:"+response.getSubMsg()+"("+response.getSubCode()+")");
        }
    }

    public AlipayTradeQueryResponse tradeQuery(String tradeNo) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setTradeNo(tradeNo);
        request.setBizModel(model);
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            return response;
        }else {
            throw new RuntimeException("支付宝交易查询失败:"+response.getSubMsg()+"("+response.getSubCode()+")");
        }
    }
}

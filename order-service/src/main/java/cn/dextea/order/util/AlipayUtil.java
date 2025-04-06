package cn.dextea.order.util;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeCreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
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

    public AlipayTradeCreateResponse tradeCreate(String orderId,String buyerOpenId, BigDecimal totalPrice){
        try{
            AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
            AlipayTradeCreateModel model = new AlipayTradeCreateModel();
            model.setOutTradeNo(orderId);
            model.setProductCode("JSAPI_PAY");
            model.setOpAppId(APP_ID);
            model.setBuyerOpenId(buyerOpenId);
            model.setTotalAmount(totalPrice.toString());
            model.setSubject("德贤茶庄");
            model.setTimeoutExpress("10m");
            request.setBizModel(model);
            AlipayTradeCreateResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                return response;
            }else {
                throw new RuntimeException("支付宝交易创建失败:"+response.getSubMsg()+"("+response.getSubCode()+")");
            }
        }catch (AlipayApiException e){
            throw new RuntimeException("支付宝交易创建失败:"+e.getMessage());
        }
    }

    public AlipayTradeQueryResponse tradeQuery(String tradeNo){
        try{
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
        }catch (AlipayApiException e){
            throw new RuntimeException("支付宝交易查询失败:"+e.getMessage());
        }
    }

    public AlipayTradeCloseResponse tradeClose(String tradeNo){
        try{
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            AlipayTradeCloseModel model = new AlipayTradeCloseModel();
            model.setTradeNo(tradeNo);
            request.setBizModel(model);
            AlipayTradeCloseResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                return response;
            }else {
                throw new RuntimeException("支付宝交易关闭失败:"+response.getSubMsg()+"("+response.getSubCode()+")");
            }
        }catch (AlipayApiException e) {
            throw new RuntimeException("支付宝交易关闭失败:" + e.getMessage());
        }
    }
}

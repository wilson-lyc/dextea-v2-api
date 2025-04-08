package cn.dextea.order.util;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
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

    public AlipayTradeRefundResponse tradeRefund(String tradeNo,BigDecimal price){
        try{
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setTradeNo(tradeNo);
            model.setRefundAmount(price.toString());
            request.setBizModel(model);
            AlipayTradeRefundResponse response= alipayClient.execute(request);
            if(response.isSuccess()){
                return response;
            }else{
                throw new RuntimeException("支付宝交易退款失败:"+response.getSubMsg()+"("+response.getSubCode()+")");
            }
        }catch (AlipayApiException e) {
            throw new RuntimeException("支付宝交易退款失败:" + e.getMessage());
        }
    }

    public AlipayTradeFastpayRefundQueryResponse tradeRefundQuery(String orderId,String tradeNo){
        try{
            AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
            AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
            model.setTradeNo(tradeNo);
            model.setOutRequestNo(orderId);
            request.setBizModel(model);
            AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                return response;
            }else{
                throw new RuntimeException("支付宝退款进度查询失败:"+response.getSubMsg()+"("+response.getSubCode()+")");
            }
        }catch (AlipayApiException e){
            throw new RuntimeException("支付宝退款进度查询失败:"+e.getMessage());
        }
    }
}

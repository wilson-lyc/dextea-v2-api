package cn.dextea.order.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.order.model.OrderPayRefundRequest;
import cn.dextea.order.model.OrderPayRefundResponse;
import cn.dextea.order.model.OrderPayDoneRequest;
import cn.dextea.order.model.OrderPayDoneResponse;
import com.alipay.api.AlipayApiException;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface StatusService {
    DexteaApiResponse<OrderPayDoneResponse> payDone(OrderPayDoneRequest data) throws AlipayApiException, NotFoundException;
    DexteaApiResponse<Void> payCancel(OrderPayDoneRequest data) throws NotFoundException, AlipayApiException;
    DexteaApiResponse<OrderPayRefundResponse> orderRefund(Long staffId, OrderPayRefundRequest data);
}

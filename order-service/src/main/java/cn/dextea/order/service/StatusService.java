package cn.dextea.order.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.order.dto.OrderUpdateStatusRequest;
import cn.dextea.order.dto.OrderPayDoneResponse;
import com.alipay.api.AlipayApiException;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface StatusService {
    DexteaApiResponse<OrderPayDoneResponse> payDone(OrderUpdateStatusRequest data) throws AlipayApiException, NotFoundException;
    DexteaApiResponse<Void> payCancel(OrderUpdateStatusRequest data) throws NotFoundException, AlipayApiException;
}

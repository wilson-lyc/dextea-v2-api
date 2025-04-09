package cn.dextea.order.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.order.model.OrderMakeDoneRequest;
import cn.dextea.order.model.OrderPayCancelRequest;
import cn.dextea.order.model.OrderPayDoneRequest;

/**
 * @author Lai Yongchao
 */
public interface StatusService {
    DexteaApiResponse<Void> payDone(OrderPayDoneRequest data);
    DexteaApiResponse<Void> payCancel(OrderPayCancelRequest data);
    DexteaApiResponse<Void> orderRefund(Long staffId,String password,String orderId);
    DexteaApiResponse<Void> makeDone(OrderMakeDoneRequest data);
}

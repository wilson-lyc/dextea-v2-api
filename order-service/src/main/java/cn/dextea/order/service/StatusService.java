package cn.dextea.order.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.order.model.OrderPayCancelRequest;
import cn.dextea.order.model.OrderPayDoneRequest;
import cn.dextea.order.model.OrderRefundRequest;

/**
 * @author Lai Yongchao
 */
public interface StatusService {
    DexteaApiResponse<Void> payDone(OrderPayDoneRequest data);
    DexteaApiResponse<Void> cancel(OrderPayCancelRequest data);
    DexteaApiResponse<Void> waitPick(String id, String mode);
    DexteaApiResponse<Void> done(String id, String mode);
    DexteaApiResponse<Void> refund(OrderRefundRequest data, String mode);
}

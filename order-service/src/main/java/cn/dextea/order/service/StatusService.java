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
    DexteaApiResponse<Void> waitPick(String id);
    DexteaApiResponse<Void> done(String id);
    DexteaApiResponse<Void> refund(OrderRefundRequest data);

    DexteaApiResponse<Void> waitPickSilent(String id);

    DexteaApiResponse<Void> doneSilent(String id);

    DexteaApiResponse<Void> refundSilent(OrderRefundRequest data);
}

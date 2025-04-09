package cn.dextea.order.service;

import cn.dextea.common.model.order.CounterOrderListModel;
import cn.dextea.common.model.order.OrderModel;

/**
 * @author Lai Yongchao
 */
public interface InternalService {
    OrderModel getOrderDetail(String id);
    void orderCallTest(Long storeId, String pickUpNo);
    CounterOrderListModel getOrderForCounter(Long storeId);
    void newOrderCallTest(Long storeId);
    void sendOrderToCounterTest(Long storeId);
}

package cn.dextea.order.service;

import cn.dextea.common.model.order.CounterOrderListModel;
import cn.dextea.common.model.order.OrderModel;

/**
 * @author Lai Yongchao
 */
public interface InternalService {
    OrderModel getOrderDetail(String id);
    void callPickUp(Long storeId, String pickUpNo);
    CounterOrderListModel getCounterOrderList(Long storeId);
    void callNewOrder(Long storeId);
    void sendNewOrder(Long storeId);
}

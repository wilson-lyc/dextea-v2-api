package cn.dextea.order.service;

import cn.dextea.common.model.order.OrderModel;

/**
 * @author Lai Yongchao
 */
public interface InternalService {
    OrderModel getOrderDetail(String id);
}

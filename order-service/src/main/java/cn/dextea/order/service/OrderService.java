package cn.dextea.order.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.order.OrderModel;
import cn.dextea.common.model.order.CounterOrderListModel;
import cn.dextea.order.model.OrderFilter;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @author Lai Yongchao
 */
public interface OrderService {
    DexteaApiResponse<IPage<OrderModel>> getOrderList(int current, int size, OrderFilter filter);
    DexteaApiResponse<OrderModel> getOrderBase(Long id);
    DexteaApiResponse<OrderModel> getOrderDetail(String id);
    DexteaApiResponse<CounterOrderListModel> getOrderListForCounter(Long id);
    DexteaApiResponse<Void> callPickUp(String id);
}

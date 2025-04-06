package cn.dextea.order.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.order.OrderModel;
import cn.dextea.order.dto.*;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface CustomerService {
    DexteaApiResponse<OrderCreateResponse> createOrder(OrderCreateRequest data);
    DexteaApiResponse<List<OrderModel>> getCustomerOrderList(Long id);
}

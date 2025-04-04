package cn.dextea.order.service;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.order.dto.OrderItemCustomerResponse;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface OrderService {
    DexteaApiResponse<OrderItemCustomerResponse> getOrderDetail(String id) throws NotFoundException;
}

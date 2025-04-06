package cn.dextea.order.service;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.common.model.order.OrderModel;
import cn.dextea.order.dto.*;
import org.apache.ibatis.javassist.NotFoundException;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface CustomerService {
    DexteaApiResponse<OrderCreateResponse> createOrder(OrderCreateRequest data);
    DexteaApiResponse<List<OrderModel>> getCustomerOrderList(Long id);
}

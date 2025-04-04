package cn.dextea.order.service;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.order.dto.*;
import com.alipay.api.AlipayApiException;
import org.apache.ibatis.javassist.NotFoundException;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface CustomerService {
    DexteaApiResponse<OrderCreateResponse> createOrder(OrderCreateRequest data);
    DexteaApiResponse<OrderPayDoneResponse> validPayDone(OrderPayDoneRequest id) throws AlipayApiException, NotFoundException;
    DexteaApiResponse<OrderItemCustomerResponse> getOrderDetail(String id) throws NotFoundException;
    DexteaApiResponse<List<OrderItemCustomerResponse>> getOrderList(Long id);
}

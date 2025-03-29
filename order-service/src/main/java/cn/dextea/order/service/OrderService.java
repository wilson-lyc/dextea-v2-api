package cn.dextea.order.service;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.order.dto.OrderCreateRequest;
import cn.dextea.order.dto.OrderCreateResponse;
import cn.dextea.order.dto.OrderListResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface OrderService {
    DexteaApiResponse<OrderCreateResponse> createOrder(@Valid OrderCreateRequest data);
    DexteaApiResponse<IPage<OrderListResponse>> geOrderList(int current, int size);
}

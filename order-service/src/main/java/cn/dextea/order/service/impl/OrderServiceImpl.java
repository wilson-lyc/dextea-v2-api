package cn.dextea.order.service.impl;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.common.pojo.Order;
import cn.dextea.common.pojo.OrderProduct;
import cn.dextea.order.dto.OrderItemCustomerResponse;
import cn.dextea.order.mapper.OrderMapper;
import cn.dextea.order.mapper.OrderProductMapper;
import cn.dextea.order.service.OrderService;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderProductMapper orderProductMapper;
    @Override
    public DexteaApiResponse<OrderItemCustomerResponse> getOrderDetail(String id) throws NotFoundException {
        MPJLambdaWrapper<Order> orderWrapper=new MPJLambdaWrapper<Order>()
                .eq(Order::getId,id)
                .selectAsClass(Order.class, OrderItemCustomerResponse.class);
        OrderItemCustomerResponse order=orderMapper.selectJoinOne(OrderItemCustomerResponse.class,orderWrapper);
        if(Objects.isNull(order)){
            throw new NotFoundException("订单不存在");
        }
        MPJLambdaWrapper<OrderProduct> productWrapper=new MPJLambdaWrapper<OrderProduct>()
                .eq(OrderProduct::getOrderId,id)
                .selectAll(OrderProduct.class);
        List<OrderProduct> products=orderProductMapper.selectJoinList(productWrapper);
        order.setProducts(products);
        return DexteaApiResponse.success(order);
    }
}

package cn.dextea.order.service.impl;

import cn.dextea.common.code.OrderStatus;
import cn.dextea.common.feign.OrderFeign;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.order.OrderModel;
import cn.dextea.common.model.order.OrderProductModel;
import cn.dextea.order.code.OrderErrorCode;
import cn.dextea.common.model.order.CounterOrderListModel;
import cn.dextea.order.model.OrderFilter;
import cn.dextea.order.mapper.OrderMapper;
import cn.dextea.order.mapper.OrderProductMapper;
import cn.dextea.order.pojo.Order;
import cn.dextea.order.pojo.OrderProduct;
import cn.dextea.order.service.OrderService;
import cn.dextea.order.websocket.util.PickUpCallUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private PickUpCallUtil pickUpCallUtil;
    @Override
    public DexteaApiResponse<IPage<OrderModel>> getOrderList(int current, int size, OrderFilter filter) {
        // 当前时间
        String nowTime=DateUtil.now();
        // 构建SQL
        MPJLambdaWrapper<Order> wrapper=new MPJLambdaWrapper<Order>()
                .selectAsClass(Order.class, OrderModel.class)
                .eqIfExists(Order::getId,filter.getId())
                .eqIfExists(Order::getTradeNo,filter.getTradeNo())
                .eqIfExists(Order::getCustomerId,filter.getCustomerId())
                .eqIfExists(Order::getStoreId,filter.getStoreId())
                .eqIfExists(Order::getDineMode,filter.getDineMode())
                .eqIfExists(Order::getPickUpNo,filter.getPickUpNo())
                .eq(Objects.nonNull(filter.getStatus())&&filter.getStatus().equals(OrderStatus.PAY_TIMEOUT.getValue()),Order::getStatus,OrderStatus.PAY_PENDING.getValue())
                .le(Objects.nonNull(filter.getStatus())&&filter.getStatus().equals(OrderStatus.PAY_TIMEOUT.getValue()),Order::getPayExpireTime, nowTime)
                .eq(Objects.nonNull(filter.getStatus())&&!filter.getStatus().equals(OrderStatus.PAY_TIMEOUT.getValue()),Order::getStatus,filter.getStatus())
                .gt(Objects.nonNull(filter.getStatus())&&filter.getStatus().equals(OrderStatus.PAY_PENDING.getValue()),Order::getPayExpireTime,nowTime);
        IPage<OrderModel> page=orderMapper.selectJoinPage(
                new Page<>(current,size),
                OrderModel.class,
                wrapper);
        if (page.getCurrent()>page.getPages())
            page=orderMapper.selectJoinPage(
                    new Page<>(page.getPages(),size),
                    OrderModel.class,
                    wrapper);
        return DexteaApiResponse.success(page);
    }

    @Override
    public DexteaApiResponse<OrderModel> getOrderBase(Long id){
        MPJLambdaWrapper<Order> wrapper=new MPJLambdaWrapper<Order>()
                .eq(Order::getId,id)
                .selectAsClass(Order.class, OrderModel.class);
        OrderModel order=orderMapper.selectJoinOne(OrderModel.class,wrapper);
        if(Objects.isNull(order))
            return DexteaApiResponse.notFound("订单不存在",
                    OrderErrorCode.ORDER_NOT_FOUND.getCode(),OrderErrorCode.ORDER_NOT_FOUND.getMsg());
        return DexteaApiResponse.success(order);
    }

    @Override
    public DexteaApiResponse<OrderModel> getOrderDetail(String id){
        // 获取订单基础信息
        MPJLambdaWrapper<Order> orderWrapper=new MPJLambdaWrapper<Order>()
                .eq(Order::getId,id)
                .selectAsClass(Order.class, OrderModel.class);
        OrderModel order=orderMapper.selectJoinOne(OrderModel.class,orderWrapper);
        if(Objects.isNull(order))
            return DexteaApiResponse.notFound("订单不存在",
                    OrderErrorCode.ORDER_NOT_FOUND.getCode(),OrderErrorCode.ORDER_NOT_FOUND.getMsg());
        // 获取订单商品
        MPJLambdaWrapper<OrderProduct> productWrapper=new MPJLambdaWrapper<OrderProduct>()
                .eq(OrderProduct::getOrderId,id)
                .selectAsClass(OrderProduct.class, OrderProductModel.class)
                .selectAs(OrderProduct::getCustomize,OrderProductModel::getCustomize);
        List<OrderProductModel> products=orderProductMapper.selectJoinList(OrderProductModel.class,productWrapper);
        order.setProducts(products);
        return DexteaApiResponse.success(order);
    }

    @Override
    public DexteaApiResponse<CounterOrderListModel> getOrderListForCounter(Long storeId) {
        CounterOrderListModel res=orderFeign.getCounterOrderList(storeId);
        return DexteaApiResponse.success(res);
    }

    @Override
    public DexteaApiResponse<Void> callPickUp(String id) {
        // 查找订单
        MPJLambdaWrapper<Order> wrapper=new MPJLambdaWrapper<Order>()
                .eq(Order::getId,id)
                .selectAll(Order.class);
        Order order=orderMapper.selectJoinOne(wrapper);
        if(Objects.isNull(order))
            return DexteaApiResponse.notFound("订单不存在",
                    OrderErrorCode.ORDER_NOT_FOUND.getCode(),OrderErrorCode.ORDER_NOT_FOUND.getMsg());
        if (order.getStatus()!=OrderStatus.WAIT_PICK.getValue() && order.getStatus()!=OrderStatus.DONE.getValue()){
            return DexteaApiResponse.fail("订单状态错误",
                    OrderErrorCode.ORDER_PICK_UP_CALL_FORBIDDEN.getCode(),OrderErrorCode.ORDER_PICK_UP_CALL_FORBIDDEN.getMsg());
        }
        pickUpCallUtil.callOnly(order.getStoreId(),order.getPickUpNo());
        return DexteaApiResponse.success();
    }
}

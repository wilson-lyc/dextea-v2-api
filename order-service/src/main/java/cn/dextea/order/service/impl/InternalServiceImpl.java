package cn.dextea.order.service.impl;

import cn.dextea.common.code.OrderStatus;
import cn.dextea.common.model.order.CounterOrderListModel;
import cn.dextea.common.model.order.OrderModel;
import cn.dextea.common.model.order.OrderProductModel;
import cn.dextea.order.mapper.OrderMapper;
import cn.dextea.order.mapper.OrderProductMapper;
import cn.dextea.order.pojo.Order;
import cn.dextea.order.pojo.OrderProduct;
import cn.dextea.order.service.InternalService;
import cn.dextea.order.websocket.util.PickUpCallUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
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
public class InternalServiceImpl implements InternalService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderProductMapper orderProductMapper;
    @Resource
    private PickUpCallUtil pickUpCallUtil;
    @Override
    public OrderModel getOrderDetail(String id) {
        // 获取订单基础信息
        MPJLambdaWrapper<Order> orderWrapper=new MPJLambdaWrapper<Order>()
                .eq(Order::getId,id)
                .selectAsClass(Order.class, OrderModel.class);
        OrderModel order=orderMapper.selectJoinOne(OrderModel.class,orderWrapper);
        if(Objects.isNull(order)){
            return null;
        }
        // 获取订单商品
        MPJLambdaWrapper<OrderProduct> productWrapper=new MPJLambdaWrapper<OrderProduct>()
                .eq(OrderProduct::getOrderId,id)
                .selectAsClass(OrderProduct.class, OrderProductModel.class)
                .selectAs(OrderProduct::getCustomize,OrderProductModel::getCustomize);
        List<OrderProductModel> products=orderProductMapper.selectJoinList(OrderProductModel.class,productWrapper);
        order.setProducts(products);
        return order;
    }

    @Override
    public void callPickUp(Long storeId, String pickUpNo) {
        pickUpCallUtil.call(storeId,pickUpNo);
    }

    @Override
    public CounterOrderListModel getCounterOrderList(Long storeId) {
        // 时间范围 - 查找5h以内的订单
        Date date = DateUtil.date();
        DateTime dateTime = DateUtil.offsetHour(date, -5);
        // 制作中订单
        MPJLambdaWrapper<Order> makingWrapper=new MPJLambdaWrapper<Order>()
                .eq(Order::getStoreId,storeId)
                .eq(Order::getStatus, OrderStatus.MAKING.getValue())
                .ge(Order::getCreateTime,dateTime.toString())
                .selectAsClass(Order.class,OrderModel.class);
        List<OrderModel> makingList=orderMapper.selectJoinList(OrderModel.class,makingWrapper);
        // 待取餐订单
        MPJLambdaWrapper<Order> waitPickWrapper=new MPJLambdaWrapper<Order>()
                .eq(Order::getStoreId,storeId)
                .eq(Order::getStatus,OrderStatus.WAIT_PICK.getValue())
                .ge(Order::getCreateTime,dateTime.toString())
                .selectAsClass(Order.class,OrderModel.class);
        List<OrderModel> waitPickList=orderMapper.selectJoinList(OrderModel.class,waitPickWrapper);
        // 已完成订单
        MPJLambdaWrapper<Order> doneWrapper=new MPJLambdaWrapper<Order>()
                .eq(Order::getStoreId,storeId)
                .eq(Order::getStatus,OrderStatus.WAIT_PICK.getValue())
                .ge(Order::getCreateTime,dateTime.toString())
                .selectAsClass(Order.class,OrderModel.class);
        List<OrderModel> doneList=orderMapper.selectJoinList(OrderModel.class,doneWrapper);
        return new CounterOrderListModel(makingList,waitPickList,doneList);
    }
}

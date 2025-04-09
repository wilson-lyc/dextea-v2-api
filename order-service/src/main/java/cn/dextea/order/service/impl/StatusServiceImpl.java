package cn.dextea.order.service.impl;

import cn.dextea.common.code.OrderStatus;
import cn.dextea.common.feign.StaffFeign;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.order.OrderModel;
import cn.dextea.order.code.OrderErrorCode;
import cn.dextea.order.model.*;
import cn.dextea.order.pojo.Order;
import cn.dextea.order.mapper.OrderMapper;
import cn.dextea.order.service.StatusService;
import cn.dextea.order.util.AlipayUtil;
import cn.dextea.order.util.PickUpNoUtil;
import cn.dextea.order.websocket.util.PickUpCallUtil;
import cn.hutool.core.date.DateUtil;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class StatusServiceImpl implements StatusService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private AlipayUtil alipayUtil;
    @Resource
    private PickUpNoUtil pickUpNoUtil;
    @Resource
    private StaffFeign staffFeign;
    @Resource
    private PickUpCallUtil pickUpCallUtil;

    @Override
    public DexteaApiResponse<Void> payDone(OrderPayDoneRequest data) {
        // 查询订单
        MPJLambdaWrapper<Order> wrapper=new MPJLambdaWrapper<Order>()
                .selectAsClass(Order.class,OrderModel.class)
                .eq(Order::getId,data.getOrderId());
        OrderModel order=orderMapper.selectJoinOne(OrderModel.class,wrapper);
        if(Objects.isNull(order)){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMsg());
        }
        // 判断订单状态 - 待支付才可以支付
        if(order.getStatus()!=OrderStatus.PAY_PENDING.getValue()){
            return DexteaApiResponse.fail("支付失败",
                    OrderErrorCode.ORDER_PAY_NOT_PENDING.getCode(),OrderErrorCode.ORDER_PAY_NOT_PENDING.getMsg());
        }
        // 请求支付宝查询交易状态
        AlipayTradeQueryResponse aliResponse=alipayUtil.tradeQuery(order.getTradeNo());
        if(aliResponse.getTradeStatus().equals("TRADE_SUCCESS")){
            // 交易成功 - 更新订单数据
            String pickUpNo= pickUpNoUtil.getPickUpNo(order.getStoreId());
            LambdaUpdateWrapper<Order> updateWrapper=new LambdaUpdateWrapper<Order>()
                    .set(Order::getPickUpNo,pickUpNo)
                    .set(Order::getStatus,OrderStatus.MAKING.getValue())
                    .set(Order::getPayTime,DateUtil.now())
                    .eq(Order::getId,data.getOrderId());
            orderMapper.update(updateWrapper);
            // 保存结果
            return DexteaApiResponse.success();
        }else{
            return DexteaApiResponse.fail("支付失败",
                    OrderErrorCode.ORDER_PAY_FAIL.getCode(),OrderErrorCode.ORDER_PAY_FAIL.getMsg());
        }
    }

    @Override
    public DexteaApiResponse<Void> payCancel(OrderPayCancelRequest data){
        // 查询订单
        MPJLambdaWrapper<Order> wrapper=new MPJLambdaWrapper<Order>()
                .selectAsClass(Order.class,OrderModel.class)
                .eq(Order::getId,data.getOrderId());
        OrderModel order=orderMapper.selectJoinOne(OrderModel.class,wrapper);
        if(Objects.isNull(order)){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMsg());
        }
        // 校验订单状态
        if(!order.getStatus().equals(OrderStatus.PAY_TIMEOUT.getValue())){
            return DexteaApiResponse.fail("订单取消失败",
                    OrderErrorCode.ORDER_PAY_NOT_PENDING.getCode(),OrderErrorCode.ORDER_PAY_NOT_PENDING.getMsg());
        }
        // 更新订单状态为取消
        LambdaUpdateWrapper<Order> updateWrapper=new LambdaUpdateWrapper<Order>()
                .set(Order::getStatus,OrderStatus.CANCEL.getValue())
                .eq(Order::getId,data.getOrderId());
        orderMapper.update(updateWrapper);
        // 支付宝关闭交易
        alipayUtil.tradeClose(order.getTradeNo());
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<Void> orderRefund(Long staffId, String password,String orderId) {
        // 验证操作者密码
        if(!staffFeign.isPasswordValid(staffId,password))
            return DexteaApiResponse.fail("退款失败",
                    OrderErrorCode.OPERATOR_PASSWORD_ILLEGAL.getCode(),OrderErrorCode.OPERATOR_PASSWORD_ILLEGAL.getMsg());
        // 查找订单
        MPJLambdaWrapper<Order> wrapper=new MPJLambdaWrapper<Order>()
                .eq(Order::getId,orderId);
        Order order=orderMapper.selectJoinOne(Order.class,wrapper);
        if (Objects.isNull(order)){
            return DexteaApiResponse.fail("退款失败",
                    OrderErrorCode.ORDER_NOT_FOUND.getCode(),OrderErrorCode.ORDER_NOT_FOUND.getMsg());
        }
        // 判断订单状态
        if(order.getStatus()<1||order.getStatus()>3){
            return DexteaApiResponse.fail("退款失败",
                    OrderErrorCode.ORDER_REFUND_FORBIDDEN.getCode(),OrderErrorCode.ORDER_REFUND_FORBIDDEN.getMsg());
        }
        // 发起退款
        alipayUtil.tradeRefund(order.getTradeNo(), BigDecimal.valueOf(0.01));
        try{
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 查询退款状态
        AlipayTradeFastpayRefundQueryResponse response=alipayUtil.tradeRefundQuery(order.getId(),order.getTradeNo());
        if(response.getRefundStatus().equals("REFUND_SUCCESS")){
            // 退款成功
            order.setStatus(OrderStatus.REFUND.getValue());
            order.setRefundTime(DateUtil.now());
            // 更新数据库 - 状态和退款时间
            orderMapper.updateById(order);
            return DexteaApiResponse.success("退款成功");
        }else{
            return DexteaApiResponse.fail("退款失败",
                    OrderErrorCode.ORDER_REFUND_FAIL.getCode(),OrderErrorCode.ORDER_REFUND_FAIL.getMsg());
        }
    }

    @Override
    public DexteaApiResponse<Void> makeDone(OrderMakeDoneRequest data) {
        // 查询订单
        MPJLambdaWrapper<Order> wrapper=new MPJLambdaWrapper<Order>()
                .eq(Order::getId,data.getOrderId());
        Order order=orderMapper.selectJoinOne(Order.class,wrapper);
        if(Objects.isNull(order)){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getCode(),OrderErrorCode.ORDER_NOT_FOUND.getMsg()
            );
        }
        // 校验状态
        if(order.getStatus()!=OrderStatus.MAKING.getValue()){
            return DexteaApiResponse.fail("订单状态错误",
                    OrderErrorCode.ORDER_STATUS_NOT_MAKING.getCode(),OrderErrorCode.ORDER_STATUS_NOT_MAKING.getMsg());
        }
        // 更新数据库
        LambdaUpdateWrapper<Order> updateWrapper=new LambdaUpdateWrapper<Order>()
                .set(Order::getStatus,OrderStatus.WAIT_PICK.getValue())
                .eq(Order::getId,data.getOrderId());
        orderMapper.update(updateWrapper);
        // 取餐叫号
        pickUpCallUtil.call(order.getStoreId(),order.getPickUpNo());
        return DexteaApiResponse.success();
    }
}

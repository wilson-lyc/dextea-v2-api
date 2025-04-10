package cn.dextea.order.service.impl;

import cn.dextea.common.code.OrderStatus;
import cn.dextea.common.feign.StaffFeign;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.order.code.OrderErrorCode;
import cn.dextea.order.model.*;
import cn.dextea.order.pojo.Order;
import cn.dextea.order.mapper.OrderMapper;
import cn.dextea.order.service.StatusService;
import cn.dextea.order.util.AlipayUtil;
import cn.dextea.order.util.PickUpNoUtil;
import cn.dextea.order.websocket.util.CounterUtil;
import cn.dextea.order.websocket.util.OrderCallUtil;
import cn.hutool.core.date.DateUtil;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
    private OrderCallUtil orderCallUtil;
    @Resource
    private CounterUtil counterUtil;

    @Override
    public DexteaApiResponse<Void> payDone(OrderPayDoneRequest data) {
        // 查询订单
        Order order=orderMapper.selectById(data.getOrderId());
        if(Objects.isNull(order)){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMsg());
        }
        // 判断订单状态 - 待支付才可以支付
        if(order.getStatus()!=OrderStatus.PAY_PENDING.getValue()){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_PAY_DONE_FORBIDDEN.getCode(),
                    OrderErrorCode.ORDER_PAY_DONE_FORBIDDEN.getMsg());
        }
        // 请求支付宝查询交易状态
        AlipayTradeQueryResponse aliResponse=alipayUtil.tradeQuery(order.getTradeNo());
        if(aliResponse.getTradeStatus().equals("TRADE_SUCCESS")){
            // 交易成功
            // 生成取餐号
            String pickUpNo= pickUpNoUtil.getPickUpNo(order.getStoreId());
            // 更新数据
            LambdaUpdateWrapper<Order> updateWrapper=new LambdaUpdateWrapper<Order>()
                    .set(Order::getPickUpNo,pickUpNo)
                    .set(Order::getStatus,OrderStatus.MAKING.getValue())
                    .set(Order::getPayTime,DateUtil.now())
                    .eq(Order::getId,data.getOrderId());
            orderMapper.update(updateWrapper);
            // 发送提醒和新订单
            counterUtil.callNewOrder(order.getStoreId());
            counterUtil.sendOrder(order.getStoreId());
            return DexteaApiResponse.success();
        }else{
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_PAY_FAIL.getCode(),
                    OrderErrorCode.ORDER_PAY_FAIL.getMsg());
        }
    }

    @Override
    public DexteaApiResponse<Void> cancel(OrderPayCancelRequest data){
        // 查询订单
        Order order=orderMapper.selectById(data.getOrderId());
        if(Objects.isNull(order)){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMsg());
        }
        // 校验状态 - 仅待支付订单可以取消
        if(!order.getStatus().equals(OrderStatus.PAY_PENDING.getValue())){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_CANCEL_FORBIDDEN.getCode(),
                    OrderErrorCode.ORDER_CANCEL_FORBIDDEN.getMsg());
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
    public DexteaApiResponse<Void> waitPick(String id) {
        // 查询订单
        Order order=orderMapper.selectById(id);
        if(Objects.isNull(order)){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMsg()
            );
        }
        // 校验状态 - 只有制作中订单可以转为待取餐
        if(order.getStatus()!=OrderStatus.MAKING.getValue()){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_STATUS_WAIT_PICK_FORBIDDEN.getCode(),
                    OrderErrorCode.ORDER_STATUS_WAIT_PICK_FORBIDDEN.getMsg());
        }
        // 更新数据库
        LambdaUpdateWrapper<Order> updateWrapper=new LambdaUpdateWrapper<Order>()
                .set(Order::getStatus,OrderStatus.WAIT_PICK.getValue())
                .eq(Order::getId,id);
        orderMapper.update(updateWrapper);
        // 取餐叫号
        orderCallUtil.callAndAddList(order.getStoreId(),order.getPickUpNo());
        // 发送新订单
        counterUtil.sendOrder(order.getStoreId());
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<Void> waitPickSilent(String id) {
        // 查询订单
        Order order=orderMapper.selectById(id);
        if(Objects.isNull(order)){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMsg()
            );
        }
        // 校验状态 - 只有制作中订单可以转为待取餐
        if(order.getStatus()!=OrderStatus.MAKING.getValue()){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_STATUS_WAIT_PICK_FORBIDDEN.getCode(),
                    OrderErrorCode.ORDER_STATUS_WAIT_PICK_FORBIDDEN.getMsg());
        }
        // 更新数据库
        LambdaUpdateWrapper<Order> updateWrapper=new LambdaUpdateWrapper<Order>()
                .set(Order::getStatus,OrderStatus.WAIT_PICK.getValue())
                .eq(Order::getId,id);
        orderMapper.update(updateWrapper);
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<Void> done(String id) {
        // 查询订单
        Order order=orderMapper.selectById(id);
        if(Objects.isNull(order)){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMsg());
        }
        // 检查状态 - 仅制作中和待取餐订单可转为已完成
        if(order.getStatus()!=OrderStatus.MAKING.getValue()&&
                order.getStatus()!=OrderStatus.WAIT_PICK.getValue()){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_STATUS_DONE_FORBIDDEN.getCode(),
                    OrderErrorCode.ORDER_STATUS_DONE_FORBIDDEN.getMsg());
        }
        // 更新状态
        LambdaUpdateWrapper<Order> updateWrapper=new LambdaUpdateWrapper<Order>()
                .set(Order::getStatus,OrderStatus.DONE.getValue())
                .eq(Order::getId,id);
        orderMapper.update(updateWrapper);
        // 发送新订单
        counterUtil.sendOrder(order.getStoreId());
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<Void> doneSilent(String id) {
        // 查询订单
        Order order=orderMapper.selectById(id);
        if(Objects.isNull(order)){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMsg());
        }
        // 检查状态 - 仅制作中和待取餐订单可转为已完成
        if(order.getStatus()!=OrderStatus.MAKING.getValue()&&
                order.getStatus()!=OrderStatus.WAIT_PICK.getValue()){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_STATUS_DONE_FORBIDDEN.getCode(),
                    OrderErrorCode.ORDER_STATUS_DONE_FORBIDDEN.getMsg());
        }
        // 更新状态
        LambdaUpdateWrapper<Order> updateWrapper=new LambdaUpdateWrapper<Order>()
                .set(Order::getStatus,OrderStatus.DONE.getValue())
                .eq(Order::getId,id);
        orderMapper.update(updateWrapper);
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<Void> refund(OrderRefundRequest data) {
        // 验证密码
        if(!staffFeign.isPasswordValid(data.getStaffId(),data.getPassword())) {
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_STATUS_REFUND_PASSWORD_ILLEGAL.getCode(),
                    OrderErrorCode.ORDER_STATUS_REFUND_PASSWORD_ILLEGAL.getMsg());
        }
        // 查询订单
        Order order=orderMapper.selectById(data.getOrderId());
        if(Objects.isNull(order)){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMsg());
        }
        // 检查状态 - 仅制作中、待取餐和已完成订单可退款
        if(order.getStatus()<1||order.getStatus()>3){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_STATUS_REFUND_FORBIDDEN.getCode(),
                    OrderErrorCode.ORDER_STATUS_REFUND_FORBIDDEN.getMsg());
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
            LambdaUpdateWrapper<Order> updateWrapper=new LambdaUpdateWrapper<Order>()
                    .set(Order::getStatus,OrderStatus.REFUND.getValue())
                    .set(Order::getRefundTime,DateUtil.now())
                    .eq(Order::getId,data.getOrderId());
            orderMapper.update(updateWrapper);
            // 发送新订单
            counterUtil.sendOrder(order.getStoreId());
            return DexteaApiResponse.success("退款成功");
        }else{
            return DexteaApiResponse.fail("退款失败",
                    OrderErrorCode.ORDER_STATUS_REFUND_FAIL.getCode(),
                    OrderErrorCode.ORDER_STATUS_REFUND_FAIL.getMsg());
        }
    }

    @Override
    public DexteaApiResponse<Void> refundSilent(OrderRefundRequest data) {
        // 验证密码
        if(!staffFeign.isPasswordValid(data.getStaffId(),data.getPassword())) {
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_STATUS_REFUND_PASSWORD_ILLEGAL.getCode(),
                    OrderErrorCode.ORDER_STATUS_REFUND_PASSWORD_ILLEGAL.getMsg());
        }
        // 查询订单
        Order order=orderMapper.selectById(data.getOrderId());
        if(Objects.isNull(order)){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_NOT_FOUND.getCode(),
                    OrderErrorCode.ORDER_NOT_FOUND.getMsg());
        }
        // 检查状态 - 仅制作中、待取餐和已完成订单可退款
        if(order.getStatus()<1||order.getStatus()>3){
            return DexteaApiResponse.fail(OrderErrorCode.ORDER_STATUS_REFUND_FORBIDDEN.getCode(),
                    OrderErrorCode.ORDER_STATUS_REFUND_FORBIDDEN.getMsg());
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
            LambdaUpdateWrapper<Order> updateWrapper=new LambdaUpdateWrapper<Order>()
                    .set(Order::getStatus,OrderStatus.REFUND.getValue())
                    .set(Order::getRefundTime,DateUtil.now())
                    .eq(Order::getId,data.getOrderId());
            orderMapper.update(updateWrapper);
            return DexteaApiResponse.success("退款成功");
        }else{
            return DexteaApiResponse.fail("退款失败",
                    OrderErrorCode.ORDER_STATUS_REFUND_FAIL.getCode(),
                    OrderErrorCode.ORDER_STATUS_REFUND_FAIL.getMsg());
        }
    }
}

package cn.dextea.order.service.impl;

import cn.dextea.common.code.OrderStatus;
import cn.dextea.common.feign.OrderFeign;
import cn.dextea.common.feign.StaffFeign;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.order.OrderModel;
import cn.dextea.order.code.OrderErrorCode;
import cn.dextea.order.code.WSMsgType;
import cn.dextea.order.model.*;
import cn.dextea.order.pojo.Order;
import cn.dextea.order.mapper.OrderMapper;
import cn.dextea.order.service.StatusService;
import cn.dextea.order.util.AlipayUtil;
import cn.dextea.order.util.AudioUtil;
import cn.dextea.order.util.PickUpNoUtil;
import cn.dextea.order.websocket.util.NewOrderUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
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
    private NewOrderUtil newOrderUtil;
    @Resource
    private OrderFeign orderFeign;
    @Resource
    private AudioUtil audioUtil;

    @Override
    public DexteaApiResponse<OrderPayDoneResponse> payDone(OrderPayDoneRequest data) {
        OrderModel order=orderFeign.getOrderDetail(data.getOrderId());
        if(Objects.isNull(order)){
            return DexteaApiResponse.fail(OrderErrorCode.NOT_FOUND.getCode(),
                    OrderErrorCode.NOT_FOUND.getMsg());
        }
        OrderPayDoneResponse payDoneResponse=new OrderPayDoneResponse();
        // 请求支付宝查询交易状态
        AlipayTradeQueryResponse aliResponse=alipayUtil.tradeQuery(data.getTradeNo());
        // 更新交易状态
        if(aliResponse.getTradeStatus().equals("TRADE_SUCCESS")){
            // 交易成功 - 更新订单数据
            String pickUpNo=pickUpNoUtil.getPickUpNo(order.getStoreId());
            LambdaUpdateWrapper<Order> wrapper=new LambdaUpdateWrapper<Order>()
                    .set(Order::getPickUpNo,pickUpNo)
                    .set(Order::getStatus,OrderStatus.MAKING.getValue())
                    .set(Order::getPayTime,DateUtil.now())
                    .eq(Order::getId,data.getOrderId());
            orderMapper.update(wrapper);
            // 保存结果
            payDoneResponse.setPayDone(true);
        }else{
            // 交易失败
            payDoneResponse.setPayDone(false);
        }
        return DexteaApiResponse.success(payDoneResponse);
    }

    @Override
    public DexteaApiResponse<Void> payCancel(OrderPayDoneRequest data) throws NotFoundException{
        MPJLambdaWrapper<Order> wrapper=new MPJLambdaWrapper<Order>()
                .eq(Order::getId,data.getOrderId())
                .eq(Order::getTradeNo,data.getTradeNo())
                .selectAll(Order.class);
        Order order=orderMapper.selectJoinOne(Order.class,wrapper);
        if(Objects.isNull(order)){
            throw new NotFoundException("订单不存在");
        }
        // 更新订单状态为取消
        order.setStatus(OrderStatus.CANCEL.getValue());
        orderMapper.updateById(order);
        // 支付宝关闭交易
        alipayUtil.tradeClose(order.getTradeNo());
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<OrderPayRefundResponse> orderRefund(Long staffId, OrderPayRefundRequest data) {
        // 验证密码
        if(!staffFeign.isPasswordValid(staffId,data.getPassword())){
            return DexteaApiResponse.fail("退款失败",
                    OrderErrorCode.PASSWORD_INVALID.getCode(),OrderErrorCode.PASSWORD_INVALID.getMsg());
        }
        // 查找订单
        MPJLambdaWrapper<Order> wrapper=new MPJLambdaWrapper<Order>()
                .eq(Order::getId,data.getId());
        Order order=orderMapper.selectJoinOne(Order.class,wrapper);
        if (Objects.isNull(order)){
            return DexteaApiResponse.fail("退款失败",
                    OrderErrorCode.NOT_FOUND.getCode(),OrderErrorCode.NOT_FOUND.getMsg());
        }
        // 判断订单状态
        if(order.getStatus()<1||order.getStatus()>3){
            return DexteaApiResponse.fail("退款失败",
                    OrderErrorCode.FORBIDDEN_REFUND.getCode(),OrderErrorCode.FORBIDDEN_REFUND.getMsg());
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
            return DexteaApiResponse.success(OrderPayRefundResponse.builder()
                    .refundDone(true)
                    .build());
        }else{
            return DexteaApiResponse.fail("退款失败",
                    OrderErrorCode.REFUND_FAIL.getCode(),OrderErrorCode.REFUND_FAIL.getMsg());
        }
    }

    @Override
    public DexteaApiResponse<Void> sendNewOrder(String id) {
        OrderModel order=orderFeign.getOrderDetail(id);
        newOrderUtil.sendMsg(order.getStoreId(),new WSMsgModel(WSMsgType.NEW_ORDER.getValue(), JSONObject.from(order)));
        String aduio=audioUtil.getAudio(101,102,8,0,0,1,103);
        newOrderUtil.sendMsg(order.getStoreId(),new WSMsgModel(WSMsgType.NEW_ORDER.getValue(), JSONObject.of("audio",aduio)));
        return DexteaApiResponse.success(aduio);
    }
}

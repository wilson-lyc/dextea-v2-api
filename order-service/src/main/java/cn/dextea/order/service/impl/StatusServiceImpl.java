package cn.dextea.order.service.impl;

import cn.dextea.common.code.OrderStatus;
import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.order.pojo.Order;
import cn.dextea.order.dto.OrderUpdateStatusRequest;
import cn.dextea.order.dto.OrderPayDoneResponse;
import cn.dextea.order.mapper.OrderMapper;
import cn.dextea.order.service.StatusService;
import cn.dextea.order.util.AlipayUtil;
import cn.dextea.order.util.PickUpNoUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

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
    @Override
    public DexteaApiResponse<OrderPayDoneResponse> payDone(OrderUpdateStatusRequest data) throws NotFoundException {
        MPJLambdaWrapper<Order> wrapper=new MPJLambdaWrapper<Order>()
                .eq(Order::getId,data.getOrderId())
                .eq(Order::getTradeNo,data.getTradeNo())
                .selectAll(Order.class);
        Order order=orderMapper.selectJoinOne(Order.class,wrapper);
        if(Objects.isNull(order)){
            throw new NotFoundException("订单不存在");
        }
        OrderPayDoneResponse payDoneResponse=new OrderPayDoneResponse();
        // 请求支付宝查询交易状态
        AlipayTradeQueryResponse aliResponse=alipayUtil.tradeQuery(data.getTradeNo());
        // 更新交易状态
        if(aliResponse.getTradeStatus().equals("TRADE_SUCCESS")){
            // 交易成功
            // 生成取餐号
            String pickUpNo=pickUpNoUtil.getPickUpNo(order.getStoreId());
            order.setPickUpNo(pickUpNo);
            // 更新订单状态为制作中
            order.setStatus(OrderStatus.MAKING.getValue());
            // 更新订单
            orderMapper.updateById(order);
            // 保存结果
            payDoneResponse.setPayDone(true);
        }else{
            // 交易失败
            payDoneResponse.setPayDone(false);
        }
        return DexteaApiResponse.success(payDoneResponse);
    }

    @Override
    public DexteaApiResponse<Void> payCancel(OrderUpdateStatusRequest data) throws NotFoundException{
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
        alipayUtil.tradeClose(data.getTradeNo());
        return DexteaApiResponse.success();
    }
}

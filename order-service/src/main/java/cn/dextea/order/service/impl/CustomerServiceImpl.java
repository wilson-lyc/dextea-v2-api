package cn.dextea.order.service.impl;

import cn.dextea.common.code.OrderStatus;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.feign.CustomerFeign;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.model.order.OrderModel;
import cn.dextea.common.model.order.OrderProductModel;
import cn.dextea.order.pojo.OrderCustomize;
import cn.dextea.order.model.*;
import cn.dextea.order.pojo.Order;
import cn.dextea.order.pojo.OrderProduct;
import cn.dextea.order.mapper.OrderMapper;
import cn.dextea.order.mapper.OrderProductMapper;
import cn.dextea.order.service.CustomerService;
import cn.dextea.order.util.AlipayUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    @Resource
    private Snowflake snowflake;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderProductMapper orderProductMapper;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private CustomerFeign customerFeign;
    @Resource
    private AlipayUtil alipayUtil;

    @Override
    public DexteaApiResponse<OrderCreateResponse> createOrder(OrderCreateRequest data) {
        // 创建订单
        Order order=Order.builder()
                .id(snowflake.nextIdStr())// 订单ID
                .storeId(data.getStoreId())// 门店ID
                .storeName(data.getStoreName())// 门店名称
                .customerId(data.getCustomerId())// 顾客ID
                .dineMode(data.getDineMode())// 用餐方式
                .status(OrderStatus.PAY_PENDING.getValue())// 订单状态，默认待支付
                .build();
        // 计算订单总价和商品数量
        BigDecimal totalPrice= new BigDecimal(0);// 订单总价
        int totalCount = 0;// 商品总数量
        // 遍历商品
        for(OrderCreateRequestProduct product:data.getProducts()){
            // 创建订单商品记录
            OrderProduct orderProduct=OrderProduct.builder()
                    .orderId(order.getId())// 订单ID
                    .id(product.getId())// 商品ID
                    .name(product.getName())// 商品名称
                    .count(product.getCount())// 购买数量
                    .cover(product.getCover())// 商品封面
                    .build();
            // 累加商品数量
            totalCount+=product.getCount();
            // 计算购买价=基础售价+客制化加价
            // 商品售价
            BigDecimal buyPrice=productFeign.getProductPrice(product.getId());
            // 计算客制化加价
            List<OrderCustomize> customizeList=new ArrayList<>();
            for(OrderCreateRequestCustomize customize:product.getCustomize()){
                BigDecimal price=productFeign.getCustomizeOptionPrice(customize.getOptionId());
                OrderCustomize productCustomize= OrderCustomize.builder()
                        .itemId(customize.getItemId())
                        .itemName(customize.getItemName())
                        .optionId(customize.getOptionId())
                        .optionName(customize.getOptionName())
                        .price(price)
                        .build();
                buyPrice = buyPrice.add(price);
                customizeList.add(productCustomize);
            }
            // 设置商品购买价
            orderProduct.setPrice(buyPrice);
            // 设置商品客制化
            orderProduct.setCustomize(customizeList);
            // 写入数据库
            orderProductMapper.insert(orderProduct);
            // 累加订单总价
            totalPrice=totalPrice.add(buyPrice.multiply(new BigDecimal(product.getCount())));
        }
        // 设置订单总价和商品数量
        order.setTotalPrice(totalPrice);
        order.setTotalCount(totalCount);
        // 获取顾客openId
        String customerOpenId=customerFeign.getCustomerOpenId(order.getCustomerId());
        // 创建交易
        AlipayTradeCreateResponse response;
        response=alipayUtil.tradeCreate(order.getId(),customerOpenId, BigDecimal.valueOf(0.01));
        order.setTradeNo(response.getTradeNo());
        // 设置过期时间
        Date date = DateUtil.date();
        DateTime payExpireTime = DateUtil.offsetMinute(date, 10);// 10min有效期
        order.setPayExpireTime(payExpireTime.toString());
        // 写入数据库
        orderMapper.insert(order);
        return DexteaApiResponse.success(OrderCreateResponse.builder()
                        .id(order.getId())
                        .totalCount(order.getTotalCount())
                        .totalPrice(order.getTotalPrice())
                        .tradeNo(order.getTradeNo())
                        .build());
    }

    @Override
    public DexteaApiResponse<List<OrderModel>> getCustomerOrderList(Long id) {
        // 获取订单列表
        MPJLambdaWrapper<Order> orderWrapper=new MPJLambdaWrapper<Order>()
                .eq(Order::getCustomerId,id)
                .selectAsClass(Order.class, OrderModel.class);
        List<OrderModel> orderList=orderMapper.selectJoinList(OrderModel.class,orderWrapper);
        // 获取商品列表
        for(OrderModel order:orderList){
            MPJLambdaWrapper<OrderProduct> productWrapper=new MPJLambdaWrapper<OrderProduct>()
                    .eq(OrderProduct::getOrderId,order.getId())
                    .selectAs(OrderProduct::getId,OrderProductModel::getId)
                    .selectAs(OrderProduct::getName,OrderProductModel::getName)
                    .selectAs(OrderProduct::getCover,OrderProductModel::getCover);
            List<OrderProductModel> products=orderProductMapper.selectJoinList(OrderProductModel.class,productWrapper);
            order.setProducts(products);
        }
        return DexteaApiResponse.success(orderList);
    }
}

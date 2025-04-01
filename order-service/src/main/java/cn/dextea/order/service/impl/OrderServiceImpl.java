package cn.dextea.order.service.impl;

import cn.dextea.common.code.OrderStatus;
import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.order.model.OrderCreateProductModel;
import cn.dextea.order.model.ProductCustomizeModel;
import cn.dextea.order.pojo.CustomizeSelected;
import cn.dextea.order.pojo.Order;
import cn.dextea.order.pojo.OrderProduct;
import cn.dextea.order.pojo.CartItem;
import cn.dextea.order.dto.OrderCreateRequest;
import cn.dextea.order.dto.OrderCreateResponse;
import cn.dextea.order.dto.OrderListResponse;
import cn.dextea.order.mapper.OrderMapper;
import cn.dextea.order.mapper.OrderProductMapper;
import cn.dextea.order.service.OrderService;
import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author Lai Yongchao
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private Snowflake snowflake;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderProductMapper orderProductMapper;
    @Resource
    private ProductFeign productFeign;

    @Override
    public DexteaApiResponse<OrderCreateResponse> createOrder(OrderCreateRequest data) {
        // 创建订单
        Order order=Order.builder()
                .id(snowflake.nextIdStr())// 生成订单ID
                .storeId(data.getStoreId())// 用餐门店ID
                .customerId(data.getCustomerId())// 顾客ID
                .dineMode(data.getDineMode())// 用餐方式
                .tableNo(data.getTableNo())// 餐桌号
                .status(OrderStatus.PAY_PENDING.getValue())// 订单状态，默认待支付
                .build();
        // 计算订单总价和商品数量
        BigDecimal totalPrice= new BigDecimal(0);// 订单总价
        int totalCount = 0;// 商品总数量
        // 遍历商品
        for(OrderCreateProductModel product:data.getProducts()){
            // 保存商品购买信息到db
            OrderProduct orderProduct=OrderProduct.builder()
                    .id(product.getId())// 商品ID
                    .count(product.getCount())// 购买数量
                    .orderId(order.getId())// 订单ID
                    .skuId(product.getSkuId())// 商品SKU
                    .build();
            // 累加商品数量
            totalCount+=product.getCount();
            // 计算商品购买价=基础售价+客制化加价
            // 商品基础售价
            BigDecimal buyPrice=productFeign.getProductPrice(product.getId());
            // 计算客制化加价
            for(ProductCustomizeModel customize:product.getCustomize()){
                BigDecimal price=productFeign.getCustomizeOptionPrice(customize.getOptionId());
                customize.setPrice(price);
                buyPrice = buyPrice.add(price);
            }
            // 设置商品购买价
            orderProduct.setPrice(buyPrice);
            // 写入db
            orderProductMapper.insert(orderProduct);
            // 累加订单总价
            totalPrice=totalPrice.add(buyPrice.multiply(new BigDecimal(product.getCount())));
        }
        // 设置订单总价和商品数量
        order.setTotalPrice(totalPrice);
        order.setTotalCount(totalCount);
        // 写入db
        orderMapper.insert(order);
        // TODO:创建支付单号
        return DexteaApiResponse.success(OrderCreateResponse.builder()
                        .id(order.getId())
                        .totalCount(order.getTotalCount())
                        .totalPrice(order.getTotalPrice())
                        .build());
    }

    @Override
    public DexteaApiResponse<IPage<OrderListResponse>> geOrderList(int current, int size) {
        MPJLambdaWrapper<Order> wrapper=new MPJLambdaWrapper<Order>()
                .selectAsClass(Order.class, OrderListResponse.class);
        IPage page=orderMapper.selectJoinPage(
                new Page<>(current,size),
                OrderListResponse.class,
                wrapper);
        if(page.getCurrent()>page.getPages())
            page=orderMapper.selectJoinPage(
                    new Page<>(page.getPages(),size),
                    OrderListResponse.class,
                    wrapper);
        return DexteaApiResponse.success(page);
    }
}

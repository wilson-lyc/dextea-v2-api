package cn.dextea.order.service.impl;

import cn.dextea.common.code.OrderStatus;
import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.pojo.CustomizeSelected;
import cn.dextea.common.pojo.Order;
import cn.dextea.common.pojo.OrderProduct;
import cn.dextea.common.pojo.CartItem;
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
                .id(snowflake.nextIdStr())
                .storeId(data.getStoreId())
                .customerId(data.getCustomerId())
                .dineMode(data.getDineMode())
                .tableNo(data.getTableNo())
                .status(OrderStatus.PAY_PENDING.getValue())
                .build();
        // 计算订单总价和商品总数量
        BigDecimal totalPrice= new BigDecimal(0);
        int totalCount = 0;
        // 遍历购物车项目
        for(CartItem cartItem:data.getCart()){
            OrderProduct orderProduct=OrderProduct.builder()
                    .orderId(order.getId())
                    .skuId(cartItem.getSkuId())
                    .productId(cartItem.getId())
                    .customize(cartItem.getCustomize())
                    .count(cartItem.getCount())
                    .build();
            // 累加商品总数量
            totalCount+=cartItem.getCount();
            // 计算购买价
            BigDecimal productPrice=productFeign.getProductPrice(cartItem.getId());
            // 计算客制化加价
            for(CustomizeSelected customize:cartItem.getCustomize()){
                BigDecimal price=productFeign.getCustomizeOptionPrice(customize.getOptionId());
                productPrice=productPrice.add(price);
            }
            // 保存购买价
            orderProduct.setPrice(productPrice);
            // 写入db
            orderProductMapper.insert(orderProduct);
            // 累加订单总价
            totalPrice=totalPrice.add(productPrice.multiply(new BigDecimal(cartItem.getCount())));
        }
        // 保存订单总价和总数量
        order.setTotalPrice(totalPrice);
        order.setTotalCount(totalCount);
        // 写入数据库
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

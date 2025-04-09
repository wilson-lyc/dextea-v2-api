package cn.dextea.order.controller;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.order.OrderModel;
import cn.dextea.common.util.DexteaJWTUtil;
import cn.dextea.order.model.*;
import cn.dextea.order.service.CustomerService;
import cn.dextea.order.service.OrderService;
import cn.dextea.order.service.StatusService;
import com.alipay.api.AlipayApiException;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lai Yongchao
 * 订单模块 - 顾客端接口
 */
@RestController
public class CustomerController {
    @Resource
    private OrderService orderService;
    @Resource
    private CustomerService customerService;
    @Resource
    private StatusService statusService;
    @Resource
    private DexteaJWTUtil jwtUtil;

    /**
     * 创建订单
     * @param data 数据
     */
    @PostMapping("/order/customer/createOrder")
    public DexteaApiResponse<OrderCreateResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest data){
        return customerService.createOrder(data);
    }

    /**
     * 获取顾客订单记录
     * @param id 顾客ID
     */
    @GetMapping("/order/customer/getOrderList")
    public DexteaApiResponse<List<OrderModel>> getCustomerOrderList(
            @RequestParam Long id,
            @RequestHeader("DexteaToken") String token){
        if(!jwtUtil.getCustomerId(token).equals(id)){
            return DexteaApiResponse.forbidden("ID不一致");
        }
        return customerService.getCustomerOrderList(id);
    }

    /**
     * 获取订单详情
     * @param id 订单ID
     */
    @GetMapping("/order/customer/getOrderDetail")
    public DexteaApiResponse<OrderModel> getOrderDetail(@RequestParam String id){
        return orderService.getOrderDetail(id);
    }

    /**
     * 完成支付 - 校验支付结果和更新数据
     * @param data 数据
     */
    @PutMapping("/order/customer/payDone")
    public DexteaApiResponse<Void> payDone(
            @RequestBody OrderPayDoneRequest data){
        return statusService.payDone(data);
    }

    /**
     * 支付取消 - 关闭交易和更新数据
     * @param data 数据
     */
    @PutMapping("/order/customer/payCancel")
    public DexteaApiResponse<Void> payCancel(
            @RequestBody OrderPayCancelRequest data){
        return statusService.payCancel(data);
    }
}

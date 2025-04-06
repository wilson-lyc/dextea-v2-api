package cn.dextea.order.controller;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.order.OrderModel;
import cn.dextea.order.dto.*;
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
    public DexteaApiResponse<List<OrderModel>> getCustomerOrderList(@RequestParam Long id){
        // TODO:鉴权 - 判断传入的ID与token的ID是否一致
        return customerService.getCustomerOrderList(id);
    }

    /**
     * 获取订单详情
     * @param id 订单ID
     */
    @GetMapping("/order/customer/getOrderDetail")
    public DexteaApiResponse<OrderModel> getOrderDetail(@RequestParam String id) throws NotFoundException {
        // TODO:鉴权 - 判断订单的顾客ID是否与token的ID是否一致
        return orderService.getOrderDetail(id);
    }

    @PutMapping("/order/customer/payDone")
    public DexteaApiResponse<OrderPayDoneResponse> payDone(
            @RequestBody OrderUpdateStatusRequest data) throws AlipayApiException, NotFoundException {
        return statusService.payDone(data);
    }

    /**
     * 支付取消
     * @param data 数据
     */
    @PutMapping("/order/customer/payCancel")
    public DexteaApiResponse<Void> payCancel(@RequestBody OrderUpdateStatusRequest data) throws NotFoundException, AlipayApiException {
        return statusService.payCancel(data);
    }
}

package cn.dextea.order.controller;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.order.dto.*;
import cn.dextea.order.service.CustomerService;
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
    private CustomerService customerService;

    @PostMapping("/order/customer/createOrder")
    public DexteaApiResponse<OrderCreateResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest data){
        return customerService.createOrder(data);
    }
    @PostMapping("/order/customer/validPayDone")
    public DexteaApiResponse<OrderPayDoneResponse> validPayDone(
            @RequestBody OrderPayDoneRequest data) throws AlipayApiException, NotFoundException {
        return customerService.validPayDone(data);
    }
    @GetMapping("/order/customer/getOrderList")
    public DexteaApiResponse<List<OrderItemCustomerResponse>> getOrderList(@RequestParam Long id){
        return customerService.getOrderList(id);
    }

    @GetMapping("/order/customer/getOrderDetail")
    public DexteaApiResponse<OrderItemCustomerResponse> getOrderDetail(@RequestParam String id) throws NotFoundException {
        return customerService.getOrderDetail(id);
    }
}

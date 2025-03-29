package cn.dextea.order.controller;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.order.dto.OrderCreateRequest;
import cn.dextea.order.dto.OrderCreateResponse;
import cn.dextea.order.dto.OrderListResponse;
import cn.dextea.order.service.OrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class OrderController {
    @Resource
    private OrderService orderService;
    @PostMapping("/order")
    public DexteaApiResponse<OrderCreateResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest data){
        return orderService.createOrder(data);
    }

    @GetMapping("/order")
    public DexteaApiResponse<IPage<OrderListResponse>> getOrderList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size){
        return orderService.geOrderList(current,size);
    }
}

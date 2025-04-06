package cn.dextea.order.controller;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.order.OrderModel;
import cn.dextea.order.dto.OrderQueryRequest;
import cn.dextea.order.service.OrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lai Yongchao
 */
@RestController
public class OrderController {
    @Resource
    private OrderService orderService;

    @GetMapping("/order")
    public DexteaApiResponse<IPage<OrderModel>> getOrderList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            OrderQueryRequest filter){
        return orderService.getOrderList(current,size,filter);
    }

    @GetMapping("/order/{id}/base")
    public DexteaApiResponse<OrderModel> getOrderBase(@PathVariable Long id) throws NotFoundException {
        return orderService.getOrderBase(id);
    }

    @GetMapping("/order/{id}")
    public DexteaApiResponse<OrderModel> getOrderDetail(@PathVariable String id) throws NotFoundException {
        return orderService.getOrderDetail(id);
    }
}

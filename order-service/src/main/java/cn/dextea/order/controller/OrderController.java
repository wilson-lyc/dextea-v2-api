package cn.dextea.order.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.order.OrderModel;
import cn.dextea.order.model.OrderPayRefundRequest;
import cn.dextea.order.model.OrderPayRefundResponse;
import cn.dextea.order.model.OrderFilter;
import cn.dextea.order.model.OrderPayDoneRequest;
import cn.dextea.order.service.OrderService;
import cn.dextea.order.service.StatusService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class OrderController {
    @Resource
    private OrderService orderService;
    @Resource
    private StatusService statusService;

    @GetMapping("/order")
    public DexteaApiResponse<IPage<OrderModel>> getOrderList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            OrderFilter filter){
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

    @PutMapping("/order/status/refund")
    public DexteaApiResponse<OrderPayRefundResponse> orderRefund(
            @RequestBody OrderPayRefundRequest data){
        Long staffId= StpUtil.getLoginIdAsLong();
        return statusService.orderRefund(staffId,data);
    }
}

package cn.dextea.order.controller;

import cn.dextea.common.model.order.OrderModel;
import cn.dextea.common.model.order.CounterOrderListModel;
import cn.dextea.order.service.InternalService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lai Yongchao
 */
@RestController
public class InternalController {
    @Resource
    private InternalService internalService;
    @GetMapping("/order/internal/getOrderDetail")
    public OrderModel getOrderDetail(@RequestParam String id) {
        return internalService.getOrderDetail(id);
    }
    @GetMapping("/order/internal/callPickUp")
    public void callPickUp(
            @RequestParam Long storeId,
            @RequestParam String pickUpNo) {
        internalService.callPickUp(storeId,pickUpNo);
    }

    @GetMapping("/order/internal/getCounterOrderList")
    public CounterOrderListModel getCounterOrderList(@RequestParam Long storeId){
        return internalService.getCounterOrderList(storeId);
    }
}

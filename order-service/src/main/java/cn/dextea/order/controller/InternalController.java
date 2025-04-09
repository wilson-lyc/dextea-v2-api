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
    @GetMapping("/order/internal/getOrderForCounter")
    public CounterOrderListModel getOrderForCounter(@RequestParam Long id){
        return internalService.getOrderForCounter(id);
    }
    @GetMapping("/order/test/orderCall")
    public void orderCallTest(
            @RequestParam Long storeId,
            @RequestParam String pickUpNo) {
        internalService.orderCallTest(storeId,pickUpNo);
    }
    @GetMapping("/order/test/newOrderCall")
    public void newOrderCallTest(@RequestParam Long storeId) {
        internalService.newOrderCallTest(storeId);
    }
    @GetMapping("/order/test/sendOrderToCounter")
    public void sendOrderToCounterTest(@RequestParam Long storeId){
        internalService.sendOrderToCounterTest(storeId);
    }
}

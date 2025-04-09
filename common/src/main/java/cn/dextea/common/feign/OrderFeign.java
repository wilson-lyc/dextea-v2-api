package cn.dextea.common.feign;

import cn.dextea.common.model.order.CounterOrderListModel;
import cn.dextea.common.model.order.OrderModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author Lai Yongchao
 */
@FeignClient("order-service")
public interface OrderFeign {
    @GetMapping("/order/internal/getOrderDetail")
    OrderModel getOrderDetail(@RequestParam("id") String id);
    @GetMapping("/order/internal/getCounterOrderList")
    CounterOrderListModel getCounterOrderList(@RequestParam("storeId") Long storeId);
}

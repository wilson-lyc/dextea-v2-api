package cn.dextea.order.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.order.OrderModel;
import cn.dextea.common.model.order.CounterOrderListModel;
import cn.dextea.order.model.OrderMakeDoneRequest;
import cn.dextea.order.model.OrderRefundRequest;
import cn.dextea.order.model.OrderFilter;
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

    /**
     * 获取订单列表
     * @param current 当前页码
     * @param size 分页大小
     * @param filter 搜索条件
     */
    @GetMapping("/order")
    public DexteaApiResponse<IPage<OrderModel>> getOrderList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            OrderFilter filter){
        return orderService.getOrderList(current,size,filter);
    }

    /**
     * 获取订单基础信息
     * @param id 订单id
     */
    @GetMapping("/order/{id}/base")
    public DexteaApiResponse<OrderModel> getOrderBase(@PathVariable Long id) throws NotFoundException {
        return orderService.getOrderBase(id);
    }

    /**
     * 获取订单详情
     * @param id 订单ID
     */
    @GetMapping("/order/{id}")
    public DexteaApiResponse<OrderModel> getOrderDetail(@PathVariable String id) throws NotFoundException {
        return orderService.getOrderDetail(id);
    }

    /**
     * 订单全额退款
     * @param data 数据
     */
    @PutMapping("/order/status/refund")
    public DexteaApiResponse<Void> orderRefund(
            @RequestBody OrderRefundRequest data){
        Long staffId= StpUtil.getLoginIdAsLong();
        return statusService.orderRefund(staffId,data.getPassword(),data.getOrderId());
    }

    /**
     * 获取订单列表（柜台）
     * @param storeId 门店ID
     */
    @GetMapping("/order/counter")
    public DexteaApiResponse<CounterOrderListModel> getOrderListForCounter(@RequestParam Long storeId){
        return orderService.getOrderListForCounter(storeId);
    }

    @PutMapping("/order/status/makeDone")
    public DexteaApiResponse<Void> makeDone(@RequestBody OrderMakeDoneRequest data){
        return statusService.makeDone(data);
    }

    @GetMapping("/order/{id}/call")
    public DexteaApiResponse<Void> callPickUp(@PathVariable String id){
        return orderService.callPickUp(id);
    }
}

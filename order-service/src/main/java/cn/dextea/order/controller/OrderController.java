package cn.dextea.order.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.order.OrderModel;
import cn.dextea.common.model.order.CounterOrderListModel;
import cn.dextea.order.model.OrderRefundRequest;
import cn.dextea.order.model.OrderFilter;
import cn.dextea.order.service.OrderService;
import cn.dextea.order.service.StatusService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
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
    public DexteaApiResponse<OrderModel> getOrderBase(@PathVariable Long id) {
        return orderService.getOrderBase(id);
    }

    /**
     * 获取订单详情
     * @param id 订单ID
     */
    @GetMapping("/order/{id}")
    public DexteaApiResponse<OrderModel> getOrderDetail(@PathVariable String id) {
        return orderService.getOrderDetail(id);
    }

    /**
     * 获取订单列表（柜台）
     * @param storeId 门店ID
     */
    @GetMapping("/order/counter")
    public DexteaApiResponse<CounterOrderListModel> getOrderForCounter(@RequestParam Long storeId){
        return orderService.getOrderForCounter(storeId);
    }

    /**
     * 取餐叫号
     * @param id 订单ID
     */
    @GetMapping("/order/{id}/call")
    public DexteaApiResponse<Void> orderCall(@PathVariable String id){
        return orderService.orderCall(id);
    }

    /*******状态*********/
    /**
     * 更新订单状态为待取餐
     * @param id 订单ID
     */
    @PutMapping("/order/{id}/status/wait-pick")
    public DexteaApiResponse<Void> waitPick(@PathVariable String id){
        return statusService.waitPick(id);
    }

    @PutMapping("/order/{id}/status/wait-pick-silent")
    public DexteaApiResponse<Void> waitPickSilent(@PathVariable String id){
        return statusService.waitPickSilent(id);
    }

    /**
     * 更新订单状态为已完成
     * @param id 订单ID
     */
    @PutMapping("/order/{id}/status/done")
    public DexteaApiResponse<Void> done(@PathVariable String id){
        return statusService.done(id);
    }

    @PutMapping("/order/{id}/status/done-silent")
    public DexteaApiResponse<Void> doneSilent(@PathVariable String id){
        return statusService.doneSilent(id);
    }

    /**
     * 更新订单状态为退款
     * @param data 退款请求
     */
    @PutMapping("/order/status/refund")
    public DexteaApiResponse<Void> refund(
            @RequestBody OrderRefundRequest data){
        // 从token获取StaffId
        Long staffId= StpUtil.getLoginIdAsLong();
        data.setStaffId(staffId);
        return statusService.refund(data);
    }

    @PutMapping("/order/status/refund-silent")
    public DexteaApiResponse<Void> refundSilent(
            @RequestBody OrderRefundRequest data){
        // 从token获取StaffId
        Long staffId= StpUtil.getLoginIdAsLong();
        data.setStaffId(staffId);
        return statusService.refundSilent(data);
    }
}

package cn.dextea.order.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
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
    @SaCheckPermission("order:order:read")
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
    @SaCheckPermission("order:order:read")
    public DexteaApiResponse<OrderModel> getOrderBase(@PathVariable Long id) {
        return orderService.getOrderBase(id);
    }

    /**
     * 获取订单详情
     * @param id 订单ID
     */
    @GetMapping("/order/{id}")
    @SaCheckPermission("order:order:read")
    public DexteaApiResponse<OrderModel> getOrderDetail(@PathVariable String id) {
        return orderService.getOrderDetail(id);
    }

    /**
     * 获取订单列表（柜台）
     * @param storeId 门店ID
     */
    @GetMapping("/order/counter")
    @SaCheckPermission("order:order:read")
    public DexteaApiResponse<CounterOrderListModel> getOrderForCounter(@RequestParam Long storeId){
        return orderService.getOrderForCounter(storeId);
    }

    /**
     * 取餐叫号
     * @param id 订单ID
     */
    @GetMapping("/order/{id}/call")
    @SaCheckPermission("order:order:call")
    public DexteaApiResponse<Void> orderCall(@PathVariable String id){
        return orderService.orderCall(id);
    }

    /*******状态*********/
    /**
     * 更新订单状态为待取餐
     * @param id 订单ID
     */
    @PutMapping("/order/{id}/status/wait-pick")
    @SaCheckPermission("order:order:update:wait_pick")
    public DexteaApiResponse<Void> waitPick(
            @PathVariable String id,
            @RequestParam(required = false) String mode){
        return statusService.waitPick(id,mode);
    }

    /**
     * 更新订单状态为已完成
     * @param id 订单ID
     */
    @PutMapping("/order/{id}/status/done")
    @SaCheckPermission("order:order:update:done")
    public DexteaApiResponse<Void> done(
            @PathVariable String id,
            @RequestParam(required = false) String mode){
        return statusService.done(id,mode);
    }

    /**
     * 更新订单状态为退款
     * @param data 退款请求
     */
    @PutMapping("/order/status/refund")
    @SaCheckPermission("order:order:update:refund")
    public DexteaApiResponse<Void> refund(
            @RequestBody OrderRefundRequest data,
            @RequestParam(required = false) String mode){
        // 从token获取StaffId
        Long staffId= StpUtil.getLoginIdAsLong();
        data.setStaffId(staffId);
        return statusService.refund(data,mode);
    }
}

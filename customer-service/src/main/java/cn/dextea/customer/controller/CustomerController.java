package cn.dextea.customer.controller;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.customer.CustomerModel;
import cn.dextea.customer.model.CustomerFilter;
import cn.dextea.customer.model.CustomerLoginRequest;
import cn.dextea.customer.service.CustomerService;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class CustomerController {
    @Resource
    private CustomerService customerService;

    /**
     * 顾客登录
     * @param data 数据
     */
    @PostMapping("/customer/login")
    public DexteaApiResponse<CustomerModel> customerLogin(@Valid @RequestBody CustomerLoginRequest data)
            throws AlipayApiException {
        return customerService.customerLogin(data);
    }

    /**
     * 获取顾客列表
     * @param current 当前页码
     * @param size 分页大小
     * @param filter 搜索条件
     */
    @GetMapping("/customer")
    public DexteaApiResponse<IPage<CustomerModel>> getCustomerList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            CustomerFilter filter){
        return customerService.getCustomerList(current,size,filter);
    }

    /**
     * 获取顾客详情
     * @param id 顾客ID
     */
    @GetMapping("/customer/{id:\\d+}")
    public DexteaApiResponse<CustomerModel> getCustomerDetail(@PathVariable Long id){
        return customerService.getCustomerDetail(id);
    }

    /**
     * 更新顾客状态
     * @param id 顾客ID
     * @param status 状态
     */
    @PutMapping("/customer/{id:\\d+}/status")
    public DexteaApiResponse<Void> updateCustomerStatus(@PathVariable Long id, @RequestParam Integer status){
        return customerService.updateCustomerStatus(id,status);
    }
}

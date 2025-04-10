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
    @PostMapping("/customer/login")
    public DexteaApiResponse<CustomerModel> customerLogin(@Valid @RequestBody CustomerLoginRequest data)
            throws AlipayApiException {
        return customerService.customerLogin(data);
    }

    @GetMapping("/customer")
    public DexteaApiResponse<IPage<CustomerModel>> getCustomerList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            CustomerFilter filter){
        return customerService.getCustomerList(current,size,filter);
    }

    @GetMapping("/customer/{id:\\d+}")
    public DexteaApiResponse<CustomerModel> getCustomerDetail(@PathVariable Long id){
        return customerService.getCustomerDetail(id);
    }

    @PutMapping("/customer/{id:\\d+}/status")
    public DexteaApiResponse<Void> updateCustomerStatus(@PathVariable Long id, @RequestParam Integer status){
        return customerService.updateCustomerStatus(id,status);
    }
}

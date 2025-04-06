package cn.dextea.customer.controller;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.customer.dto.CustomerListResponse;
import cn.dextea.customer.dto.CustomerLoginRequest;
import cn.dextea.customer.dto.CustomerLoginResponse;
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
    @PostMapping("/customer")
    public DexteaApiResponse<CustomerLoginResponse> customerLogin(@Valid @RequestBody CustomerLoginRequest data)
            throws AlipayApiException {
        return customerService.customerLogin(data);
    }

    @GetMapping("/customer")
    public DexteaApiResponse<IPage<CustomerListResponse>> getCustomerList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size){
        return customerService.getCustomerList(current,size);
    }
}

package cn.dextea.customer.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.customer.dto.CustomerLoginDTO;
import cn.dextea.customer.service.CustomerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lai Yongchao
 */
@RestController
public class CustomerController {
    @Resource
    private CustomerService customerService;
    @PostMapping("/customer")
    public ApiResponse customerLogin(@Valid @RequestBody CustomerLoginDTO data){
        return customerService.customerLogin(data);
    }
}

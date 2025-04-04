package cn.dextea.customer.controller;

import cn.dextea.customer.service.InternalService;
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

    @GetMapping("/customer/internal/getCustomerOpenId")
    public String getCustomerOpenId(@RequestParam Long id){
        return internalService.getCustomerOpenId(id);
    }
}

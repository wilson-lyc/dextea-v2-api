package cn.dextea.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("customer-service")
public interface CustomerFeign {
    @GetMapping("/customer/internal/getCustomerOpenId")
    String getCustomerOpenId(@RequestParam("id") Long id);
    @GetMapping("/customer/internal/verifyCustomerToken")
    boolean verifyCustomerToken(@RequestParam("token") String token);
}

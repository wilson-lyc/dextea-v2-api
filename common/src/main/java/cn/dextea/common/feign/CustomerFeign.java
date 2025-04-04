package cn.dextea.common.feign;

import cn.dextea.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient("customer-service")
public interface CustomerFeign {
    @GetMapping("/customer/internal/getCustomerOpenId")
    String getCustomerOpenId(@RequestParam("id") Long id);
}

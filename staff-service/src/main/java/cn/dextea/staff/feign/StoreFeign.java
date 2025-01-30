package cn.dextea.staff.feign;

import cn.dextea.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("store-service")
public interface StoreFeign {
    @GetMapping("/store/{id}")
    ApiResponse getStoreById(@PathVariable("id") Integer id);
}

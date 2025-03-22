package cn.dextea.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("store-service")
public interface StoreFeign {
    @GetMapping("/store/internal/isStoreIdValid")
    boolean isStoreIdValid(@RequestParam("id") Long id);
}

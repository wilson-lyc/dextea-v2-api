package cn.dextea.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("store-service")
public interface StoreFeign {
    @GetMapping("/store/internal/{id}/valid")
    boolean isStoreIdValid(@PathVariable("id") Long id);
}

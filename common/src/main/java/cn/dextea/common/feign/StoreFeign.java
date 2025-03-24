package cn.dextea.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lai Yongchao
 */
@FeignClient("store-service")
public interface StoreFeign {
    @GetMapping("/store/internal/isStoreIdValid")
    boolean isStoreIdValid(@RequestParam("id") Long id);
    @GetMapping("/store/internal/getStoreName")
    String getStoreName(@RequestParam("id") Long id);
}

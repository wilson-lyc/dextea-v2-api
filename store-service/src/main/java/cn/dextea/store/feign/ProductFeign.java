package cn.dextea.store.feign;

import cn.dextea.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Lai Yongchao
 */
@FeignClient("product-service")
public interface ProductFeign {
    @GetMapping("/menu/{id:\\d+}")
    ApiResponse getMenuById(@PathVariable("id") Long id);
}

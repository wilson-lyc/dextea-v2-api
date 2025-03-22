package cn.dextea.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Lai Yongchao
 */
@FeignClient("product-service")
public interface ProductFeign {
    @GetMapping("/product/internal/{id:\\d+}/valid")
    boolean isProductIdValid(@PathVariable("id") Long id);

    @GetMapping("/product-category/internal/{id:\\d+}/valid")
    boolean isCategoryIdValid(@PathVariable("id") Long id);
}

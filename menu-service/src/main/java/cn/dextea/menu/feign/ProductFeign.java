package cn.dextea.menu.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lai Yongchao
 */
@FeignClient("product-service")
public interface ProductFeign {
    @GetMapping("/product/internal/isProductIdValid")
    boolean isProductIdValid(@RequestParam("id") Long id);

    @GetMapping("/product/internal/isCategoryIdValid")
    boolean isCategoryIdValid(@RequestParam("id") Long id);

    @GetMapping("/product/internal/isCustomizeItemIdValid")
    boolean isCustomizeItemIdValid(@RequestParam("id") Long id);

    @GetMapping("/product/internal/isCustomizeOptionIdValid")
    boolean isCustomizeOptionIdValid(@RequestParam("id") Long id);
}

package cn.dextea.common.feign;

import cn.dextea.common.pojo.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

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

    @GetMapping("/product/internal/getProductStoreStatus")
    Integer getProductStoreStatus(@RequestParam("productId") Long productId, @RequestParam("storeId") Long storeId);

    @GetMapping("/product/internal/getProductGlobalStatus")
    Integer getProductGlobalStatus(@RequestParam("productId") Long productId);

    @GetMapping("/product/internal/getProductById")
    Product getProductById(@RequestParam("productId") Long productId);

    @GetMapping("/product/internal/getProductById")
    Product getProductById(
            @RequestParam("productId") Long productId,
            @RequestParam("storeId") Long storeId);

    @GetMapping("/product/internal/getOptionGlobalStatus")
    Integer getOptionGlobalStatus(@RequestParam("optionId") Long optionId);

    @GetMapping("/product/internal/getOptionStoreStatus")
    Integer getOptionStoreStatus(
            @RequestParam("optionId") Long optionId,
            @RequestParam("storeId") Long storeId);

    @GetMapping("/product/internal/getCustomizeOptionPrice")
    BigDecimal getCustomizeOptionPrice(@RequestParam("id") Long id);

    @GetMapping("/product/internal/getProductPrice")
    BigDecimal getProductPrice(@RequestParam("id") Long id);
}

package cn.dextea.common.feign;

import cn.dextea.common.model.product.ProductModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@FeignClient("product-service")
public interface ProductFeign {
    // ID校验
    @GetMapping("/product/internal/isCategoryIdValid")
    boolean isCategoryIdValid(@RequestParam("id") Long id);

    @GetMapping("/product/internal/isProductIdValid")
    boolean isProductIdValid(@RequestParam("id") Long id);

    @GetMapping("/product/internal/isCustomizeItemIdValid")
    boolean isCustomizeItemIdValid(@RequestParam("id") Long id);

    @GetMapping("/product/internal/isCustomizeOptionIdValid")
    boolean isCustomizeOptionIdValid(@RequestParam("id") Long id);

    // 获取商品状态
    @GetMapping("/product/internal/getProductGlobalStatus")
    Integer getProductGlobalStatus(@RequestParam("productId") Long productId);

    @GetMapping("/product/internal/getProductStoreStatus")
    Integer getProductStoreStatus(@RequestParam("productId") Long productId,
                                  @RequestParam("storeId") Long storeId);

    // 获取客制化选项状态
    @GetMapping("/product/internal/getOptionGlobalStatus")
    Integer getOptionGlobalStatus(@RequestParam("optionId") Long optionId);

    @GetMapping("/product/internal/getOptionStoreStatus")
    Integer getOptionStoreStatus(@RequestParam("optionId") Long optionId,
                                 @RequestParam("storeId") Long storeId);

    // 获取价格
    @GetMapping("/product/internal/getProductPrice")
    BigDecimal getProductPrice(@RequestParam("id") Long id);

    @GetMapping("/product/internal/getCustomizeOptionPrice")
    BigDecimal getCustomizeOptionPrice(@RequestParam("id") Long id);

    // 获取商品详情
    @GetMapping("/product/internal/getProductDetail")
    ProductModel getProductDetail(@RequestParam("productId") Long productId);

    @GetMapping("/product/internal/getProductDetail")
    ProductModel getProductDetail(@RequestParam("productId") Long productId,
                                  @RequestParam("storeId") Long storeId);

    // 获取商品基础信息
    @GetMapping("/product/internal/getProductBase")
    ProductModel getProductBase(@RequestParam("productId") Long productId);
    @GetMapping("/product/internal/getProductBase")
    ProductModel getProductBase(@RequestParam("productId") Long productId,
                                @RequestParam("storeId") Long storeId);
}

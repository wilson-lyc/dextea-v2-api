package cn.dextea.product.controller;

import cn.dextea.common.model.product.ProductModel;
import cn.dextea.product.service.InternalService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@RestController
public class InternalController {
    @Resource
    private InternalService internalService;

    // ID校验
    @GetMapping("/product/internal/isCategoryIdValid")
    public boolean isCategoryIdValid(@RequestParam Long id) {
        return internalService.isCategoryIdValid(id);
    }

    @GetMapping("/product/internal/isProductIdValid")
    public boolean isProductIdValid(@RequestParam Long id) {
        return internalService.isProductIdValid(id);
    }

    @GetMapping("/product/internal/isCustomizeItemIdValid")
    public boolean isCustomizeItemIdValid(@RequestParam Long id) {
        return internalService.isCustomizeItemIdValid(id);
    }

    @GetMapping("/product/internal/isCustomizeOptionIdValid")
    public boolean isCustomizeOptionIdValid(@RequestParam Long id) {
        return internalService.isCustomizeOptionIdValid(id);
    }

    // 获取商品状态
    @GetMapping("/product/internal/getProductGlobalStatus")
    public Integer getProductGlobalStatus(@RequestParam Long productId) {
        return internalService.getProductGlobalStatus(productId);
    }

    @GetMapping("/product/internal/getProductStoreStatus")
    public Integer getProductStoreStatus(
            @RequestParam Long productId,
            @RequestParam Long storeId) {
        return internalService.getProductStoreStatus(productId,storeId);
    }

    // 获取客制化选项状态
    @GetMapping("/product/internal/getOptionGlobalStatus")
    public Integer getOptionGlobalStatus(@RequestParam Long optionId) {
        return internalService.getOptionGlobalStatus(optionId);
    }

    @GetMapping("/product/internal/getOptionStoreStatus")
    public Integer getOptionStoreStatus(
            @RequestParam Long optionId,
            @RequestParam Long storeId){
        return internalService.getOptionStoreStatus(optionId,storeId);
    }

    // 获取价格
    @GetMapping("/product/internal/getProductPrice")
    public BigDecimal getProductPrice(@RequestParam Long id){
        return internalService.getProductPrice(id);
    }

    @GetMapping("/product/internal/getCustomizeOptionPrice")
    public BigDecimal getCustomizeOptionPrice(@RequestParam Long id){
        return internalService.getCustomizeOptionPrice(id);
    }

    // 获取商品详情
    @GetMapping("/product/internal/getProductDetail")
    public ProductModel getProductById(
            @RequestParam Long productId,
            @RequestParam(required = false) Long storeId) {
        if (Objects.isNull(storeId))
            return internalService.getProductDetail(productId);
        else
            return internalService.getProductDetail(productId,storeId);
    }
}

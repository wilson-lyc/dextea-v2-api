package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.product.ProductQueryDTO;
import cn.dextea.product.service.StoreService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class StoreController {
    @Resource
    private StoreService storeService;

    /**
     * 获取门店商品列表
     * 返回商品的门店状态
     * @param storeId 门店ID
     * @param current 当前页码
     * @param size 每页数量
     * @param filter 筛选条件
     */
    @GetMapping("/store/{storeId:\\d+}/product")
    public ApiResponse getStoreProductList(
            @PathVariable Long storeId,
            @Min(value = 1,message = "current不能小于1") Integer current,
            @Min(value = 1,message = "size不能小于1") Integer size,
            @Valid ProductQueryDTO filter) {
        return storeService.getStoreProductList(storeId,current,size,filter);
    }

    /**
     * 获取商品的门店状态
     * @param storeId 门店ID
     * @param productId 商品ID
     */
    @GetMapping("/store/{storeId:\\d+}/product/{productId:\\d+}/status")
    public ApiResponse getProductStoreStatus(
            @PathVariable Long storeId,
            @PathVariable Long productId){
        return storeService.getProductStoreStatus(storeId,productId);
    }

    /**
     * 更新商品的门店状态
     * @param storeId 门店ID
     * @param productId 商品ID
     * @param status 门店状态
     */
    @PutMapping("/store/{storeId:\\d+}/product/{productId:\\d+}/status")
    public ApiResponse updateProductStoreStatus(
            @PathVariable Long storeId,
            @PathVariable Long productId,
            @RequestParam Integer status){
        return storeService.updateProductStoreStatus(storeId,productId,status);
    }
}

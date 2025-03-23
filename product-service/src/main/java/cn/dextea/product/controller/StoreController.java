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

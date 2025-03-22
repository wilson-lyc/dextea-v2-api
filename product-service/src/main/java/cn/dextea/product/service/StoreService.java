package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.product.ProductQueryDTO;

/**
 * @author Lai Yongchao
 */
public interface StoreService {
    // 商品
    ApiResponse getStoreProductList(Long storeId, Integer current, Integer size, ProductQueryDTO filter);
    ApiResponse getProductStoreStatus(Long storeId, Long productId);
    ApiResponse updateProductStoreStatus(Long storeId, Long productId, Integer status);
    // 客制化选项
    ApiResponse getStoreOption(Long storeId, Long itemId);
    ApiResponse getOptionStoreStatus(Long storeId, Long optionId);
    ApiResponse updateOptionStoreStatus(Long storeId, Long optionId, Integer status);
}

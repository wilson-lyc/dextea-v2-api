package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.product.ProductQueryDTO;

/**
 * @author Lai Yongchao
 */
public interface StoreService {
    ApiResponse getStoreProductList(Long storeId, Integer current, Integer size, ProductQueryDTO filter);
    ApiResponse getProductStoreStatus(Long storeId, Long productId);
    ApiResponse updateProductStoreStatus(Long storeId, Long productId, Integer status);
}

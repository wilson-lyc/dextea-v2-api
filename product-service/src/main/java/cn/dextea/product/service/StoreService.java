package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.ProductQueryDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

/**
 * @author Lai Yongchao
 */
public interface StoreService {
    ApiResponse getStoreProductList(Long storeId, Integer current, Integer size, ProductQueryDTO filter);
    ApiResponse getProductStoreStatus(Long storeId, Long productId);
    ApiResponse updateProductStoreStatus(Long storeId, Long productId, Integer status);
}

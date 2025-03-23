package cn.dextea.menu.service;

import cn.dextea.common.dto.ApiResponse;

/**
 * @author Lai Yongchao
 */
public interface ProductService {
    ApiResponse menuBindProduct(Long groupId, Long productId, Integer sort);
    ApiResponse menuUnbindProduct(Long groupId, Long productId);
    ApiResponse getMenuBindProductInfo(Long groupId, Long productId);
    ApiResponse updateMenuBindProductInfo(Long groupId, Long productId, Integer sort);
    ApiResponse getProductList(Long groupId);
    ApiResponse getProductList(Long storeId, Long groupId);
}

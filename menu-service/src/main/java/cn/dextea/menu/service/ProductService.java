package cn.dextea.menu.service;

import cn.dextea.common.dto.ApiResponse;

/**
 * @author Lai Yongchao
 */
public interface ProductService {
    ApiResponse addProduct(Long menuId, String groupId, Long productId, Integer sort);
    ApiResponse deleteProduct(Long menuId, String groupId, Long productId);
    ApiResponse getProductList(Long menuId, String groupId);
    ApiResponse getProductInfo(Long menuId, String groupId, Long productId);
    ApiResponse updateProductInfo(Long menuId, String groupId, Long productId, Integer sort);
}

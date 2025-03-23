package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.product.ProductCreateDTO;
import cn.dextea.product.dto.product.ProductQueryDTO;
import cn.dextea.product.dto.product.ProductUpdateBaseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

/**
 * @author Lai Yongchao
 */
public interface ProductService {
    // 管理端
    // 创建
    ApiResponse createProduct(ProductCreateDTO data);
    // 列表
    ApiResponse getProductList(int current,int size, ProductQueryDTO filter);
    ApiResponse getProductList(Long storeId, int current,int size, ProductQueryDTO filter);
    ApiResponse getProductOption(Integer status);
    // 单项
    ApiResponse getProductBase(Long id);
    ApiResponse getProductImg(Long id);
    ApiResponse getProductStatus(Long productId);
    ApiResponse getProductStatus(Long productId, Long storeId);
    // 更新
    ApiResponse updateProductBase(Long id, ProductUpdateBaseDTO data);
    ApiResponse updateProductStatus(Long productId, Integer status);
    ApiResponse updateProductStatus(Long productId, Long storeId, Integer status);
}

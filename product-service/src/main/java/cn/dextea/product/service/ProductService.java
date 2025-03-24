package cn.dextea.product.service;

import cn.dextea.common.code.ProductStatus;
import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.product.ProductCreateDTO;
import cn.dextea.product.dto.product.ProductQueryDTO;
import cn.dextea.product.dto.product.ProductUpdateBaseDTO;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface ProductService {
    // 管理端
    // 创建
    ApiResponse createProduct(ProductCreateDTO data) throws NotFoundException;
    // 列表
    ApiResponse getProductList(int current,int size, ProductQueryDTO filter);
    ApiResponse getProductList(Long storeId, int current,int size, ProductQueryDTO filter) throws NotFoundException;
    ApiResponse getProductOption(Integer status);
    // 单项
    ApiResponse getProductBase(Long id) throws NotFoundException;
    ApiResponse getProductImg(Long id) throws NotFoundException;
    ApiResponse getProductStatus(Long productId) throws NotFoundException;
    ApiResponse getProductStatus(Long productId, Long storeId) throws NotFoundException;
    // 更新
    ApiResponse updateProductBase(Long id, ProductUpdateBaseDTO data) throws NotFoundException;
    ApiResponse updateProductStatus(Long productId, ProductStatus status);
    ApiResponse updateProductStatus(Long productId, Long storeId, ProductStatus status) throws NotFoundException;
}

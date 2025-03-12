package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.ProductCreateDTO;
import cn.dextea.product.dto.ProductQueryDTO;
import cn.dextea.product.pojo.Product;

/**
 * @author Lai Yongchao
 */
public interface ProductService {
    // 管理端
    // 创建
    ApiResponse createProduct(ProductCreateDTO data);
    // 列表
    ApiResponse getProductList(int current,int size, ProductQueryDTO filter);
    ApiResponse getProductOption(Integer status);
    // 单项
    ApiResponse getProductBase(Long id);
    ApiResponse getProductImg(Long id);
    ApiResponse getProductGlobalStatus(Long id);
    // 更新
    ApiResponse updateProduct(Long id, Product product);
}

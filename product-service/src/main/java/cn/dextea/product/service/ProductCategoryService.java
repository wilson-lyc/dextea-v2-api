package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.ProductCategoryDTO;

/**
 * @author Lai Yongchao
 */
public interface ProductCategoryService {
    ApiResponse create(ProductCategoryDTO data);
    ApiResponse update(Long id, ProductCategoryDTO data);
    ApiResponse getById(Long id);
    ApiResponse getAll();
    ApiResponse getOptions();
}

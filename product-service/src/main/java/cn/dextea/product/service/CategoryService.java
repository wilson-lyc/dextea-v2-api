package cn.dextea.product.service;

import cn.dextea.common.model.common.ApiResponse;
import cn.dextea.product.dto.category.CategoryDTO;

/**
 * @author Lai Yongchao
 */
public interface CategoryService {
    ApiResponse createCategory(CategoryDTO data);
    ApiResponse getCategoryList();
    ApiResponse getCategoryById(Long id);
    ApiResponse getCategoryOption();
    ApiResponse updateCategory(Long id, CategoryDTO data);

}

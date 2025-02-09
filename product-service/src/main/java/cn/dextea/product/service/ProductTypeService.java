package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.EditProductTypeDTO;

/**
 * @author Lai Yongchao
 */
public interface ProductTypeService {
    ApiResponse create(EditProductTypeDTO data);
    ApiResponse update(Long id, EditProductTypeDTO data);
    ApiResponse getById(Long id);
    ApiResponse getAll();
    ApiResponse getSelectOptions();
}

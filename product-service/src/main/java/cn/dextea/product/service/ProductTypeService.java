package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.ProductTypeDTO;

/**
 * @author Lai Yongchao
 */
public interface ProductTypeService {
    ApiResponse create(ProductTypeDTO data);
    ApiResponse update(Long id,ProductTypeDTO data);
    ApiResponse getById(Long id);
    ApiResponse getAll();
}

package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CreateProductDTO;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface ProductService {
    ApiResponse create(CreateProductDTO data);
    ApiResponse getProductById(Long id);
}

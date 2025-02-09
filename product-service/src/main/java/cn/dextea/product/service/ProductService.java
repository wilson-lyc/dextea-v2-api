package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CreateProductDTO;
import cn.dextea.product.dto.SearchProductDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

/**
 * @author Lai Yongchao
 */
public interface ProductService {
    ApiResponse create(CreateProductDTO data);
    ApiResponse getProductById(Long id);
    ApiResponse getProductList(@Valid @Min(value = 1,message = "current不能小于1") int current, @Valid @Min(value = 1,message = "size不能小于1") int size, @Valid SearchProductDTO filter);
}

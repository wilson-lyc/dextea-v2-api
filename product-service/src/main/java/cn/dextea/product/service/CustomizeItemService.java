package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CustomizeItemCreateDTO;
import cn.dextea.product.dto.CustomizeItemUpdateDTO;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface CustomizeItemService {
    ApiResponse create(CustomizeItemCreateDTO data);
    ApiResponse getList(Long productId);
    ApiResponse getById(Long id);
    ApiResponse update(Long id, @Valid CustomizeItemUpdateDTO data);
}

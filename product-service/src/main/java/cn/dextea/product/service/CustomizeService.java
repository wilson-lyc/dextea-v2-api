package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CreateCustomizeDTO;
import cn.dextea.product.dto.UpdateCustomizeDTO;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface CustomizeService {
    ApiResponse create(CreateCustomizeDTO data);
    ApiResponse getCustomizeList(Long productId);
    ApiResponse getCustomizeById(Long id);
    ApiResponse update(Long id, @Valid UpdateCustomizeDTO data);
}

package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CreateCustomizeOptionDTO;
import cn.dextea.product.dto.UpdateCustomizeOptionDTO;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface CustomizeOptionService {
    ApiResponse create(@Valid CreateCustomizeOptionDTO data);
    ApiResponse getCustomizeOptionList(Long customizeId);
    ApiResponse getCustomizeOptionById(Long id);
    ApiResponse updateCustomizeOption(Long id, @Valid UpdateCustomizeOptionDTO data);
}

package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.MenuGroupCreateDTO;
import cn.dextea.product.dto.MenuTypeUpdateDTO;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface MenuGroupService {
    ApiResponse create(@Valid MenuGroupCreateDTO data);
    ApiResponse getById(Long id);
    ApiResponse getList(Long id);
    ApiResponse update(Long id, @Valid MenuTypeUpdateDTO data);
}

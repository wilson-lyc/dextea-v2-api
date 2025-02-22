package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.*;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface MenuService {
    ApiResponse create(@Valid MenuCreateDTO data);
    ApiResponse getList(int current, int size, MenuQueryDTO filter);
    ApiResponse getById(Long id);
    ApiResponse update(Long id, MenuBaseUpdateDTO data);
}

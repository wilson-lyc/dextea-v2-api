package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.*;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface MenuService {
    ApiResponse createMenu(@Valid MenuCreateDTO data);
    ApiResponse getMenuListByFilter(int current, int size, MenuFilterDTO filter);
    ApiResponse getMenuBaseById(Long id);
    ApiResponse updateMenuBase(Long id, MenuBaseUpdateDTO data);
}

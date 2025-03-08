package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.*;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface MenuService {
    ApiResponse createMenu(@Valid MenuCreateDTO data);
    ApiResponse getMenuList(int current, int size, MenuQueryDTO filter);
    ApiResponse getMenuById(Long id);
    ApiResponse updateMenu(Long id, MenuBaseUpdateDTO data);
    ApiResponse getMenuProducts(Long id);
}

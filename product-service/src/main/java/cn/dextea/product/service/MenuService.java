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

    // 基础信息
    ApiResponse getMenuBaseById(Long id);
    ApiResponse updateMenuBase(Long id, MenuBaseUpdateDTO data);

    // 菜单分类
    ApiResponse createMenuType(@Valid MenuTypeCreateDTO data);
    ApiResponse getMenuTypeBaseById(Long id);
    ApiResponse getMenuTypeList(Long menuId);
    ApiResponse updateMenuTypeBase(Long id, @Valid MenuTypeUpdateDTO data);
}

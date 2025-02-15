package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.MenuTypeCreateDTO;
import cn.dextea.product.dto.MenuTypeUpdateDTO;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface MenuTypeService {
    ApiResponse createMenuType(@Valid MenuTypeCreateDTO data);
    ApiResponse getMenuTypeBaseById(Long id);
    ApiResponse getMenuTypeListByMenuId(Long menuId);
    ApiResponse updateMenuTypeBaseById(Long id, @Valid MenuTypeUpdateDTO data);
}

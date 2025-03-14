package service;

import cn.dextea.common.dto.ApiResponse;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface MenuService {
    ApiResponse createMenu(@Valid MenuCreateDTO data);
    ApiResponse getMenuList(int current, int size, MenuQueryDTO filter);
    ApiResponse getMenuBaseById(Long id);
    ApiResponse updateMenu(Long id, MenuBaseUpdateDTO data);
    ApiResponse getMenuById(Long id);
}

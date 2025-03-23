package cn.dextea.menu.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.menu.dto.MenuEditDTO;
import cn.dextea.menu.dto.MenuQueryDTO;

/**
 * @author Lai Yongchao
 */
public interface MenuService {
    ApiResponse createMenu(MenuEditDTO data);
    ApiResponse getMenuList(int current, int size, MenuQueryDTO filter);
    ApiResponse getMenuInfo(Long id);
    ApiResponse updateMenuInfo(Long id, MenuEditDTO data);
}

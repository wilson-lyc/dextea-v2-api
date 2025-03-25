package cn.dextea.menu.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.menu.dto.menu.MenuCreateDTO;
import cn.dextea.menu.dto.menu.MenuQueryDTO;
import cn.dextea.menu.dto.menu.MenuUpdateBaseDTO;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface MenuService {
    ApiResponse createMenu(MenuCreateDTO data);
    ApiResponse getMenuList(int current, int size, MenuQueryDTO filter);
    ApiResponse getMenuById(Long id, Long storeId) throws NotFoundException;
    ApiResponse getMenuBase(Long id) throws NotFoundException;
    ApiResponse updateMenuBase(Long id, MenuUpdateBaseDTO data) throws NotFoundException;
}

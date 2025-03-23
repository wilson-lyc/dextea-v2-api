package cn.dextea.menu.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.MenuGroupCreateDTO;
import cn.dextea.product.dto.MenuTypeUpdateDTO;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface GroupService {
    ApiResponse createMenuGroup(@Valid MenuGroupCreateDTO data);
    ApiResponse getMenuGroupById(Long id);
    ApiResponse getMenuGroupList(Long id);
    ApiResponse updateMenuGroup(Long id, @Valid MenuTypeUpdateDTO data);
}

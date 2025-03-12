package controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.MenuGroupCreateDTO;
import cn.dextea.product.dto.MenuTypeUpdateDTO;
import cn.dextea.product.service.MenuGroupService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
@RestController
public class GroupController {
    @Resource
    private MenuGroupService menuGroupService;

    /**
     * 创建菜单分组
     * @param data {name}
     */
    @PostMapping("/menu/group")
    public ApiResponse createMenuGroup(@Valid @RequestBody MenuGroupCreateDTO data){
        return menuGroupService.createMenuGroup(data);
    }

    /**
     * 获取菜单分组的基础信息
     * @param id 菜单分组id
     */
    @GetMapping("/menu/group/{id:\\d+}")
    public ApiResponse getMenuGroupById(@PathVariable Long id){
        return menuGroupService.getMenuGroupById(id);
    }

    /**
     * 更新菜单分组的基础信息
     * @param id 菜单分组id
     * @param data {name}
     */
    @PutMapping("/menu/group/{id:\\d+}")
    public ApiResponse updateMenuGroup(@PathVariable Long id, @Valid @RequestBody MenuTypeUpdateDTO data){
        return menuGroupService.updateMenuGroup(id, data);
    }

    /**
     * 根据MenuId获取分组列表
     * @param id 菜单id
     */
    @GetMapping("/menu/{id:\\d+}/group")
    public ApiResponse getMenuGroupList(@PathVariable Long id){
        return menuGroupService.getMenuGroupList(id);
    }
}

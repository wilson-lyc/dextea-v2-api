package cn.dextea.menu.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.menu.dto.GroupEditDTO;
import cn.dextea.menu.service.GroupService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class GroupController {
    @Resource
    private GroupService groupService;

    /**
     * 创建菜单分组
     * @param data {name}
     */
    @PostMapping("/menu/{menuId:\\d+}/group")
    public ApiResponse createGroup(
            @PathVariable Long menuId,
            @Valid @RequestBody GroupEditDTO data){
        return groupService.createGroup(menuId,data);
    }

    /**
     * 获取菜单的分组列表
     * @param menuId 菜单id
     */
    @GetMapping("/menu/{menuId:\\d+}/group")
    public ApiResponse getGroupList(@PathVariable Long menuId){
        return groupService.getGroupList(menuId);
    }

    @GetMapping("/menu/group/{groupId:\\d+}")
    public ApiResponse getGroupInfo(@PathVariable Long groupId){
        return groupService.getGroupInfo(groupId);
    }

    /**
     * 更新菜单分组的基础信息
     * @param groupId 菜单分组id
     * @param data {name}
     */
    @PutMapping("/menu/group/{groupId:\\d+}")
    public ApiResponse updateGroupInfo(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupEditDTO data){
        return groupService.updateGroupInfo(groupId, data);
    }
}

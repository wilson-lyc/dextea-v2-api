package cn.dextea.menu.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.menu.model.group.GroupBaseModel;
import cn.dextea.menu.model.group.GroupCreateRequest;
import cn.dextea.menu.model.group.GroupListModel;
import cn.dextea.menu.model.group.GroupUpdateBaseRequest;
import cn.dextea.menu.pojo.MenuGroup;
import cn.dextea.menu.service.GroupService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@RestController
public class GroupController {
    @Resource
    private GroupService groupService;

    @PostMapping("/menu/{menuId:\\d+}/group")
    @SaCheckPermission("menu:group:create")
    public DexteaApiResponse<Void> createGroup(
            @PathVariable Long menuId,
            @Valid @RequestBody GroupCreateRequest data){
        return groupService.createGroup(menuId,data);
    }

    @GetMapping("/menu/{menuId:\\d+}/group")
    @SaCheckPermission("menu:group:read")
    public DexteaApiResponse<List<GroupListModel>> getGroupList(
            @PathVariable Long menuId){
        return groupService.getGroupList(menuId);
    }

    @GetMapping("/menu/{menuId:\\d+}/group/{groupId}")
    @SaCheckPermission("menu:group:read")
    public DexteaApiResponse<MenuGroup> getGroupById(
            @PathVariable Long menuId,
            @PathVariable String groupId) throws NotFoundException{
        return groupService.getGroupById(menuId,groupId);
    }

    @GetMapping("/menu/{menuId:\\d+}/group/{groupId}/base")
    @SaCheckPermission("menu:group:read")
    public DexteaApiResponse<GroupBaseModel> getGroupBase(
            @PathVariable Long menuId,
            @PathVariable String groupId) {
        return groupService.getGroupBase(menuId,groupId);
    }

    @PutMapping("/menu/{menuId:\\d+}/group/{groupId}/base")
    @SaCheckPermission("menu:group:update:base")
    public DexteaApiResponse<Void> updateGroupBase(
            @PathVariable Long menuId,
            @PathVariable String groupId,
            @Valid @RequestBody GroupUpdateBaseRequest data) {
        return groupService.updateGroupBase(menuId,groupId,data);
    }

    @DeleteMapping("/menu/{menuId:\\d+}/group/{groupId}")
    @SaCheckPermission("menu:group:delete")
    public DexteaApiResponse<Void> deleteGroup(
            @PathVariable Long menuId,
            @PathVariable String groupId) {
        return groupService.deleteGroup(menuId,groupId);
    }

}

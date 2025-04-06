package cn.dextea.menu.controller;

import cn.dextea.common.model.common.ApiResponse;
import cn.dextea.menu.dto.group.GroupCreateDTO;
import cn.dextea.menu.dto.group.GroupUpdateBaseDTO;
import cn.dextea.menu.service.GroupService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class GroupController {
    @Resource
    private GroupService groupService;

    @PostMapping("/menu/{menuId:\\d+}/group")
    public ApiResponse createGroup(
            @PathVariable Long menuId,
            @Valid @RequestBody GroupCreateDTO data){
        return groupService.createGroup(menuId,data);
    }

    @GetMapping("/menu/{menuId:\\d+}/group")
    public ApiResponse getGroupList(
            @PathVariable Long menuId){
        return groupService.getGroupList(menuId);
    }

    @GetMapping("/menu/{menuId:\\d+}/group/{groupId}")
    public ApiResponse getGroupById(
            @PathVariable Long menuId,
            @PathVariable String groupId) throws NotFoundException{
        return groupService.getGroupById(menuId,groupId);
    }

    @GetMapping("/menu/{menuId:\\d+}/group/{groupId}/base")
    public ApiResponse getGroupBase(
            @PathVariable Long menuId,
            @PathVariable String groupId) throws NotFoundException {
        return groupService.getGroupBase(menuId,groupId);
    }

    @PutMapping("/menu/{menuId:\\d+}/group/{groupId}/base")
    public ApiResponse updateGroupBase(
            @PathVariable Long menuId,
            @PathVariable String groupId,
            @Valid @RequestBody GroupUpdateBaseDTO data) throws NotFoundException {
        return groupService.updateGroupBase(menuId,groupId,data);
    }

    @DeleteMapping("/menu/{menuId:\\d+}/group/{groupId}")
    public ApiResponse deleteGroup(
            @PathVariable Long menuId,
            @PathVariable String groupId) throws NotFoundException {
        return groupService.deleteGroup(menuId,groupId);
    }

}

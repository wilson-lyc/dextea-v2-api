package cn.dextea.auth.controller;

import cn.dextea.auth.model.RoleCreateRequest;
import cn.dextea.auth.model.RoleUpdateRequest;
import cn.dextea.auth.service.RoleService;
import cn.dextea.common.model.auth.RoleModel;
import cn.dextea.common.model.common.DexteaApiResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@RestController
public class RoleController {
    @Resource
    private RoleService roleService;

    /**
     * 创建角色
     * @param data 数据
     */
    @PostMapping("/role")
    public DexteaApiResponse<Void> createRole(@Valid @RequestBody RoleCreateRequest data){
        return roleService.createRole(data);
    }

    /**
     * 获取角色列表
     */
    @GetMapping("/role")
    public DexteaApiResponse<List<RoleModel>> getRoleList(){
        return roleService.getRoleList();
    }

    /**
     * 获取角色详情
     * @param id 角色ID
     */
    @GetMapping("/role/{id:\\d+}")
    public DexteaApiResponse<RoleModel> getRoleDetail(@PathVariable Long id){
        return roleService.getRoleDetail(id);
    }

    /**
     * 更新角色基础信息
     * @param id 角色ID
     * @param data 数据
     */
    @PutMapping("/role/{id:\\d+}")
    public DexteaApiResponse<Void> updateRoleDetail(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest data){
        return roleService.updateRoleDetail(id, data);
    }
}

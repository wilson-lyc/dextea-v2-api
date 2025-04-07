package cn.dextea.auth.controller;

import cn.dextea.auth.service.StaffService;
import cn.dextea.common.model.auth.PermissionModel;
import cn.dextea.common.model.auth.RoleModel;
import cn.dextea.common.model.common.DexteaApiResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@RestController
public class StaffController {
    @Resource
    private StaffService staffService;

    /**
     * 员工绑定角色
     * @param roleId 角色ID
     * @param staffId 员工ID
     */
    @PostMapping("/role/{roleId:\\d+}/staff/{staffId:\\d+}")
    public DexteaApiResponse<Void> addStaffToRole(
            @PathVariable Long roleId,
            @PathVariable Long staffId){
        return staffService.addStaffToRole(roleId,staffId);
    }

    /**
     * 员工解绑角色
     * @param roleId 角色ID
     * @param staffId 员工ID
     */
    @DeleteMapping("/role/{roleId:\\d+}/staff/{staffId:\\d+}")
    public DexteaApiResponse<Void> deleteStaffFromRole(
            @PathVariable Long roleId,
            @PathVariable Long staffId){
        return staffService.deleteStaffFromRole(roleId,staffId);
    }

    /**
     * 获取员工的角色列表
     * @param staffId 员工ID
     */
    @GetMapping("/staff/{staffId:\\d+}/role")
    public DexteaApiResponse<List<RoleModel>> getStaffRoleList(
            @PathVariable Long staffId){
        return staffService.getStaffRoleList(staffId);
    }

    @GetMapping("/staff/{staffId:\\d+}/permission")
    public DexteaApiResponse<List<PermissionModel>> getStaffPermissionList(
            @PathVariable Long staffId){
        return staffService.getStaffPermissionList(staffId);
    }
}

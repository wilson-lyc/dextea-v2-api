package cn.dextea.staff.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.dto.request.BindRolePermissionRequest;
import cn.dextea.staff.dto.request.CreateRoleRequest;
import cn.dextea.staff.dto.request.RolePageQueryRequest;
import cn.dextea.staff.dto.request.UpdateRoleRequest;
import cn.dextea.staff.dto.response.CreateRoleResponse;
import cn.dextea.staff.dto.response.RoleDetailResponse;
import cn.dextea.staff.service.RoleAdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色管理
 */
@RestController
@RequestMapping("/v1/admin/roles")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class RoleAdminController {
    private final RoleAdminService roleAdminService;

    /**
     * 创建角色
     * @param request 创建角色请求参数
     * @return 创建结果
     */
    @PostMapping
    public ApiResponse<CreateRoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        return roleAdminService.create(request);
    }

    /**
     * 分页查询角色列表
     * @param request 角色分页查询请求参数
     * @return 角色分页数据
     */
    @GetMapping
    public ApiResponse<IPage<RoleDetailResponse>> getRolePage(@Valid RolePageQueryRequest request) {
        return roleAdminService.page(request);
    }

    /**
     * 查询角色详情
     * @param id 角色ID
     * @return 角色详情
     */
    @GetMapping("/{id}")
    public ApiResponse<RoleDetailResponse> getRoleDetail(
            @PathVariable("id") @Min(value = 1, message = "角色ID不能为空") Long id) {
        return roleAdminService.detail(id);
    }

    /**
     * 更新角色信息
     * @param id 角色ID
     * @param request 更新角色请求参数
     * @return 更新后的角色详情
     */
    @PutMapping("/{id}")
    public ApiResponse<RoleDetailResponse> updateRole(
            @PathVariable("id") @Min(value = 1, message = "角色ID不能为空") Long id,
            @Valid @RequestBody UpdateRoleRequest request) {
        return roleAdminService.update(id, request);
    }

    /**
     * 删除角色
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(
            @PathVariable("id") @Min(value = 1, message = "角色ID不能为空") Long id) {
        return roleAdminService.delete(id);
    }

    /**
     * 为角色绑定权限
     * @param id 角色ID
     * @param request 角色绑定权限请求参数
     * @return 绑定结果
     */
    @PostMapping("/{id}/permissions")
    public ApiResponse<Void> bindPermission(
            @PathVariable("id") @Min(value = 1, message = "角色ID不能为空") Long id,
            @Valid @RequestBody BindRolePermissionRequest request) {
        return roleAdminService.bindPermission(id, request);
    }

    /**
     * 解除角色的权限
     * @param id 角色ID
     * @param permissionName 权限名称
     * @return 解除结果
     */
    @DeleteMapping("/{id}/permissions/{permissionName}")
    public ApiResponse<Void> unbindPermission(
            @PathVariable("id") @Min(value = 1, message = "角色ID不能为空") Long id,
            @PathVariable("permissionName") @NotBlank(message = "权限名称不能为空") String permissionName) {
        return roleAdminService.unbindPermission(id, permissionName);
    }
}

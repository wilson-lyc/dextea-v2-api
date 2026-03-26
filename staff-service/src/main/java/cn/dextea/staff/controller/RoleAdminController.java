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

@RestController
@RequestMapping("/v1/admin/roles")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class RoleAdminController {
    private final RoleAdminService roleAdminService;

    @PostMapping
    public ApiResponse<CreateRoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        return roleAdminService.createRole(request);
    }

    @GetMapping
    public ApiResponse<IPage<RoleDetailResponse>> getRolePage(@Valid RolePageQueryRequest request) {
        return roleAdminService.getRolePage(request);
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleDetailResponse> getRoleDetail(@PathVariable @Min(value = 1, message = "角色ID不能为空") Long id) {
        return roleAdminService.getRoleDetail(id);
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleDetailResponse> updateRole(
            @PathVariable @Min(value = 1, message = "角色ID不能为空") Long id,
            @Valid @RequestBody UpdateRoleRequest request) {
        return roleAdminService.updateRole(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable @Min(value = 1, message = "角色ID不能为空") Long id) {
        return roleAdminService.deleteRole(id);
    }

    @PostMapping("/{id}/permissions")
    public ApiResponse<Void> bindPermission(
            @PathVariable @Min(value = 1, message = "角色ID不能为空") Long id,
            @Valid @RequestBody BindRolePermissionRequest request) {
        return roleAdminService.bindPermission(id, request);
    }

    @DeleteMapping("/{id}/permissions/{permissionName}")
    public ApiResponse<Void> unbindPermission(
            @PathVariable @Min(value = 1, message = "角色ID不能为空") Long id,
            @PathVariable @NotBlank(message = "权限名称不能为空") String permissionName) {
        return roleAdminService.unbindPermission(id, permissionName);
    }
}

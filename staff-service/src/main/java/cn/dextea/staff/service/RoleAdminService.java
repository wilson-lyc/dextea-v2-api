package cn.dextea.staff.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.dto.request.BindRolePermissionRequest;
import cn.dextea.staff.dto.request.CreateRoleRequest;
import cn.dextea.staff.dto.request.RolePageQueryRequest;
import cn.dextea.staff.dto.request.UpdateRoleRequest;
import cn.dextea.staff.dto.response.CreateRoleResponse;
import cn.dextea.staff.dto.response.RoleDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface RoleAdminService {
    ApiResponse<CreateRoleResponse> createRole(CreateRoleRequest request);

    ApiResponse<IPage<RoleDetailResponse>> getRolePage(RolePageQueryRequest request);

    ApiResponse<RoleDetailResponse> getRoleDetail(Long id);

    ApiResponse<RoleDetailResponse> updateRole(Long id, UpdateRoleRequest request);

    ApiResponse<Void> deleteRole(Long id);

    ApiResponse<Void> bindPermission(Long id, BindRolePermissionRequest request);

    ApiResponse<Void> unbindPermission(Long id, String permissionName);
}

package cn.dextea.auth.service;

import cn.dextea.auth.model.RoleCreateRequest;
import cn.dextea.auth.model.RoleUpdateRequest;
import cn.dextea.common.model.auth.PermissionModel;
import cn.dextea.common.model.auth.RoleModel;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.staff.StaffModel;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface RoleService {
    DexteaApiResponse<Void> createRole(RoleCreateRequest data);
    DexteaApiResponse<List<RoleModel>> getRoleList();
    DexteaApiResponse<RoleModel> getRoleDetail(Long id);
    DexteaApiResponse<Void> updateRoleDetail(Long id, RoleUpdateRequest data);
    DexteaApiResponse<List<PermissionModel>> getPermissionList();
    DexteaApiResponse<List<StaffModel>> getRoleStaffList(Long id);
}

package cn.dextea.auth.service;

import cn.dextea.common.model.auth.PermissionModel;
import cn.dextea.common.model.auth.RoleModel;
import cn.dextea.common.model.common.DexteaApiResponse;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface StaffService {
    DexteaApiResponse<Void> addStaffToRole(Long roleId, Long staffId);
    DexteaApiResponse<Void> deleteStaffFromRole(Long roleId, Long staffId);
    DexteaApiResponse<List<RoleModel>> getStaffRoleList(Long staffId);
    DexteaApiResponse<List<PermissionModel>> getStaffPermissionList(Long staffId);
}

package cn.dextea.auth.service;

import cn.dextea.auth.dto.RoleDTO;
import cn.dextea.common.dto.ApiResponse;

/**
 * @author Lai Yongchao
 */
public interface RoleService {
    ApiResponse create(RoleDTO data);
    ApiResponse getRoleList(int current, int size);
    ApiResponse getRoleById(Long id);
    ApiResponse update(Long id, RoleDTO data);
    ApiResponse getStaffRoleByUid(Long uid);
    ApiResponse getStaffRoleKeyByUid(Long uid);
    ApiResponse getStaffRouterByUid(Long uid);
}

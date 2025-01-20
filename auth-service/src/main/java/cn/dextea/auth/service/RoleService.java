package cn.dextea.auth.service;

import cn.dextea.auth.dto.RoleDTO;
import cn.dextea.common.dto.ApiResponse;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface RoleService {
    ApiResponse create(RoleDTO data);
    ApiResponse getRoleList(int current, int size);
    ApiResponse getRoleById(Long id);
    ApiResponse update(Long id, RoleDTO data);
    ApiResponse getRoleByStaffId(Long uid);
}

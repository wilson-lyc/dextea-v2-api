package cn.dextea.auth.service;

import cn.dextea.auth.pojo.Permission;
import cn.dextea.common.dto.ApiResponse;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface PermissionService {
    ApiResponse getPermissionList();
    ApiResponse getPermissionByStaffId(Long uid);
    ApiResponse getPermissionKeyByStaffId(Long uid);
}

package cn.dextea.auth.service.impl;

import cn.dextea.auth.mapper.PermissionMapper;
import cn.dextea.auth.pojo.Permission;
import cn.dextea.auth.pojo.RolePermission;
import cn.dextea.auth.pojo.StaffRole;
import cn.dextea.auth.service.PermissionService;
import cn.dextea.common.dto.ApiResponse;
import com.alibaba.fastjson2.JSONObject;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    PermissionMapper permissionMapper;
    @Override
    public ApiResponse getPermissionList() {
        List<Permission> permissions = permissionMapper.selectList(null);
        return ApiResponse.success(JSONObject.of("permissions",permissions));
    }

    @Override
    public ApiResponse getPermissionByStaffId(Long uid) {
        MPJLambdaWrapper<Permission> wrapper = new MPJLambdaWrapper<Permission>()
                .selectAll(Permission.class)
                .innerJoin(RolePermission.class, RolePermission::getPermissionId, Permission::getId)
                .innerJoin(StaffRole.class, StaffRole::getRoleId, RolePermission::getRoleId)
                .eq(StaffRole::getStaffId, uid);
        List<Permission> permissions = permissionMapper.selectList(wrapper);
        return ApiResponse.success(JSONObject.of("permissions",permissions));
    }

    @Override
    public ApiResponse getPermissionKeyByStaffId(Long uid) {
        MPJLambdaWrapper<Permission> wrapper = new MPJLambdaWrapper<Permission>()
                .select(Permission::getKey)
                .innerJoin(RolePermission.class, RolePermission::getPermissionId, Permission::getId)
                .innerJoin(StaffRole.class, StaffRole::getRoleId, RolePermission::getRoleId)
                .eq(StaffRole::getStaffId, uid);
        List<String> permissionKeys = permissionMapper.selectObjs(wrapper);
        return ApiResponse.success(JSONObject.of("permissionKeys",permissionKeys));
    }
}

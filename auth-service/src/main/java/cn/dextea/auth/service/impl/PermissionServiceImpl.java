package cn.dextea.auth.service.impl;

import cn.dextea.auth.mapper.PermissionMapper;
import cn.dextea.auth.pojo.Permission;
import cn.dextea.auth.service.PermissionService;
import cn.dextea.common.dto.ApiResponse;
import com.alibaba.fastjson2.JSONObject;
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
}

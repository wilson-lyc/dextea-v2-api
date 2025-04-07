package cn.dextea.auth.service.impl;

import cn.dextea.auth.code.AuthErrorCode;
import cn.dextea.auth.mapper.PermissionMapper;
import cn.dextea.auth.mapper.RoleMapper;
import cn.dextea.auth.pojo.Permission;
import cn.dextea.auth.pojo.Role;
import cn.dextea.auth.service.PermissionService;
import cn.dextea.common.model.auth.PermissionModel;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.common.SelectOptionModel;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class PermissionServiceImpl implements PermissionService {
    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public DexteaApiResponse<List<PermissionModel>> getPermissionList() {
        MPJLambdaWrapper<Permission> wrapper = new MPJLambdaWrapper<Permission>()
                .selectAsClass(Permission.class,PermissionModel.class);
        List<PermissionModel> list = permissionMapper.selectJoinList(PermissionModel.class, wrapper);
        return DexteaApiResponse.success(list);
    }
}

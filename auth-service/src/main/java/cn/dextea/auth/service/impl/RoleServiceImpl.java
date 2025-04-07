package cn.dextea.auth.service.impl;

import cn.dextea.auth.code.AuthErrorCode;
import cn.dextea.auth.model.RoleCreateRequest;
import cn.dextea.auth.model.RoleUpdateRequest;
import cn.dextea.auth.mapper.RoleMapper;
import cn.dextea.auth.service.RoleService;
import cn.dextea.common.model.auth.RoleModel;
import cn.dextea.auth.pojo.Role;
import cn.dextea.common.model.common.DexteaApiResponse;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleMapper roleMapper;

    @Override
    public DexteaApiResponse<Void> createRole(RoleCreateRequest data) {
        Role role=data.toRole();
        // 校验角色名
        String regex = "^[a-zA-Z0-9_]+$";
        if (!data.getName().matches(regex)){
            return DexteaApiResponse.fail("参数错误", AuthErrorCode.ROLE_NAME_ILLEGAL.getCode(), AuthErrorCode.ROLE_NAME_ILLEGAL.getMsg());
        }
        roleMapper.insert(role);
        return DexteaApiResponse.success("角色创建成功");
    }

    @Override
    public DexteaApiResponse<List<RoleModel>> getRoleList() {
        MPJLambdaWrapper<Role> wrapper=new MPJLambdaWrapper<Role>()
                .selectAs(Role::getId, RoleModel::getId)
                .selectAs(Role::getName, RoleModel::getName)
                .selectAs(Role::getDescription, RoleModel::getDescription);
        List<RoleModel> roles=roleMapper.selectJoinList(RoleModel.class,wrapper);
        return DexteaApiResponse.success(roles);
    }

    @Override
    public DexteaApiResponse<RoleModel> getRoleDetail(Long id){
        MPJLambdaWrapper<Role> wrapper=new MPJLambdaWrapper<Role>()
                .selectAsClass(Role.class, RoleModel.class)
                .eq(Role::getId,id);
        RoleModel role=roleMapper.selectJoinOne(RoleModel.class,wrapper);
        if (Objects.isNull(role)){
            return DexteaApiResponse.notFound(AuthErrorCode.ROLE_NOT_FOUND.getCode(), AuthErrorCode.ROLE_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success(role);
    }

    @Override
    public DexteaApiResponse<Void> updateRoleDetail(Long id, RoleUpdateRequest data){
        Role role=data.toRole(id);
        // 校验角色名
        String regex = "^[a-zA-Z0-9_]+$";
        if (!data.getName().matches(regex)){
            return DexteaApiResponse.fail("参数错误", AuthErrorCode.ROLE_NAME_ILLEGAL.getCode(), AuthErrorCode.ROLE_NAME_ILLEGAL.getMsg());
        }
        if (roleMapper.updateById(role)==0){
            return DexteaApiResponse.fail("更新失败", AuthErrorCode.ROLE_NOT_FOUND.getCode(), AuthErrorCode.ROLE_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success("更新成功");
    }
}

package cn.dextea.auth.service.impl;

import cn.dextea.auth.mapper.PermissionMapper;
import cn.dextea.auth.mapper.StaffRoleMapper;
import cn.dextea.auth.pojo.Permission;
import cn.dextea.auth.pojo.Role;
import cn.dextea.auth.pojo.StaffRole;
import cn.dextea.auth.service.InternalService;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class InternalServiceImpl implements InternalService {
    @Resource
    private StaffRoleMapper staffRoleMapper;
    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public List<String> getStaffRoleKeys(Long id) {
        MPJLambdaWrapper<StaffRole> wrapper = new MPJLambdaWrapper<StaffRole>()
                .innerJoin(Role.class,Role::getId,StaffRole::getRoleId)
                .eq(StaffRole::getStaffId, id)
                .select(Role::getName);
        return staffRoleMapper.selectJoinList(String.class, wrapper);
    }

    @Override
    public List<String> getStaffPermissionKeys(Long id) {
        MPJLambdaWrapper<StaffRole> roleWrapper = new MPJLambdaWrapper<StaffRole>()
                .selectAll(Role.class)
                .innerJoin(Role.class,Role::getId,StaffRole::getRoleId)
                .eq(StaffRole::getStaffId, id);
        List<Role> roles = staffRoleMapper.selectJoinList(Role.class, roleWrapper);
        List<Long> permissionIds=roles.stream().map(Role::getPermissions).flatMap(List::stream).toList();
        if(permissionIds.isEmpty()){
            return List.of();
        }
        MPJLambdaWrapper<Permission> permissionWrapper=new MPJLambdaWrapper<Permission>()
                .select(Permission::getName)
                .in(Role::getId,permissionIds);
        return permissionMapper.selectJoinList(String.class,permissionWrapper);
    }
}

package cn.dextea.auth.service.impl;

import cn.dextea.auth.code.AuthErrorCode;
import cn.dextea.auth.mapper.PermissionMapper;
import cn.dextea.auth.mapper.RoleMapper;
import cn.dextea.auth.mapper.StaffRoleMapper;
import cn.dextea.auth.pojo.Permission;
import cn.dextea.auth.pojo.Role;
import cn.dextea.auth.pojo.StaffRole;
import cn.dextea.auth.service.StaffService;
import cn.dextea.common.feign.StaffFeign;
import cn.dextea.common.model.auth.PermissionModel;
import cn.dextea.common.model.auth.RoleModel;
import cn.dextea.common.model.common.DexteaApiResponse;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class StaffServiceImpl implements StaffService {
    @Resource
    private StaffRoleMapper staffRoleMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private StaffFeign staffFeign;

    @Override
    public DexteaApiResponse<Void> addStaffToRole(Long roleId, Long staffId) {
        // 验证角色ID
        if (!roleMapper.exists(new MPJLambdaWrapper<Role>().eq(Role::getId, roleId))) {
            return DexteaApiResponse.fail("绑定失败",
                    AuthErrorCode.ROLE_NOT_FOUND.getCode(),
                    AuthErrorCode.ROLE_NOT_FOUND.getMsg());
        }
        // 验证员工ID
        if (!staffFeign.isStaffIdValid(staffId)) {
            return DexteaApiResponse.fail("绑定失败",
                    AuthErrorCode.STAFF_NOT_FOUND.getCode(),
                    AuthErrorCode.STAFF_NOT_FOUND.getMsg());
        }
        // 验证员工是否已经绑定该角色
        if (staffRoleMapper.exists(new MPJLambdaWrapper<StaffRole>()
                .eq(StaffRole::getStaffId, roleId)
                .eq(StaffRole::getStaffId, staffId))) {
            return DexteaApiResponse.fail("绑定失败",
                    AuthErrorCode.STAFF_BIND_ROLE_EXISTED.getCode(),
                    AuthErrorCode.STAFF_BIND_ROLE_EXISTED.getMsg());
        }
        // 绑定
        StaffRole staffRole = StaffRole.builder()
                .staffId(staffId)
                .roleId(roleId)
                .build();
        if (staffRoleMapper.insert(staffRole) == 0) {
            return DexteaApiResponse.fail("绑定失败",
                    AuthErrorCode.STAFF_BIND_ROLE_FAILED.getCode(),
                    AuthErrorCode.STAFF_BIND_ROLE_FAILED.getMsg());
        }
        return DexteaApiResponse.success("绑定成功");
    }

    @Override
    public DexteaApiResponse<Void> deleteStaffFromRole(Long roleId, Long staffId) {
        // 验证员工ID
        if (!staffFeign.isStaffIdValid(staffId)) {
            return DexteaApiResponse.fail("解绑失败",
                    AuthErrorCode.STAFF_NOT_FOUND.getCode(),
                    AuthErrorCode.STAFF_NOT_FOUND.getMsg());
        }
        // 验证员工是否已经绑定该角色
        if (!staffRoleMapper.exists(new MPJLambdaWrapper<StaffRole>()
                .eq(StaffRole::getRoleId, roleId)
                .eq(StaffRole::getStaffId, staffId))) {
            return DexteaApiResponse.fail("解绑失败",
                    AuthErrorCode.STAFF_BIND_ROLE_NOT_EXISTED.getCode(),
                    AuthErrorCode.STAFF_BIND_ROLE_NOT_EXISTED.getMsg());
        }
        // 解绑
        if (staffRoleMapper.delete(new MPJLambdaWrapper<StaffRole>()
                .eq(StaffRole::getRoleId, roleId)
                .eq(StaffRole::getStaffId, staffId)) == 0) {
            return DexteaApiResponse.fail("解绑失败",
                    AuthErrorCode.STAFF_UNBIND_ROLE_FAILED.getCode(),
                    AuthErrorCode.STAFF_UNBIND_ROLE_FAILED.getMsg());
        }
        return DexteaApiResponse.success("解绑成功");
    }

    @Override
    public DexteaApiResponse<List<RoleModel>> getStaffRoleList(Long staffId) {
        // 验证员工ID
        if (!staffFeign.isStaffIdValid(staffId)) {
            return DexteaApiResponse.fail(AuthErrorCode.STAFF_NOT_FOUND.getCode(),
                    AuthErrorCode.STAFF_NOT_FOUND.getMsg());
        }
        // 获取角色列表
        MPJLambdaWrapper<StaffRole> wrapper = new MPJLambdaWrapper<StaffRole>()
                .selectAll(Role.class)
                .innerJoin(Role.class, Role::getId, StaffRole::getRoleId)
                .eq(StaffRole::getStaffId, staffId);
        List<Role> roles = staffRoleMapper.selectJoinList(Role.class, wrapper);
        List<RoleModel> roleModelList=new ArrayList<>();
        // 遍历每个角色
        for(Role role:roles){
            RoleModel roleModel=RoleModel.builder()
                    .id(role.getId())
                    .name(role.getName())
                    .description(role.getDescription())
                    .build();
            if(!role.getPermissions().isEmpty()){
                // 获取权限列表
                MPJLambdaWrapper<Permission> permissionWrapper=new MPJLambdaWrapper<Permission>()
                        .selectAsClass(Permission.class,PermissionModel.class)
                        .in(Permission::getId,role.getPermissions());
                List<PermissionModel> permissions = permissionMapper.selectJoinList(PermissionModel.class, permissionWrapper);
                roleModel.setPermissions(permissions);
            }else{
                roleModel.setPermissions(List.of());
            }
            roleModelList.add(roleModel);
        }
        return DexteaApiResponse.success(roleModelList);
    }

    @Override
    public DexteaApiResponse<List<PermissionModel>> getStaffPermissionList(Long staffId) {
        // 验证员工ID
        if (!staffFeign.isStaffIdValid(staffId)) {
            return DexteaApiResponse.fail(AuthErrorCode.STAFF_NOT_FOUND.getCode(),
                    AuthErrorCode.STAFF_NOT_FOUND.getMsg());
        }
        // 获取角色列表
        MPJLambdaWrapper<StaffRole> roleWrapper= new MPJLambdaWrapper<StaffRole>()
                .selectAll(Role.class)
                .innerJoin(Role.class, Role::getId, StaffRole::getRoleId)
                .eq(StaffRole::getStaffId, staffId);
        List<Role> roles = staffRoleMapper.selectJoinList(Role.class, roleWrapper);
        List<Long> permissionIds=roles.stream().map(Role::getPermissions).flatMap(List::stream).toList();
        if(permissionIds.isEmpty()){
            return DexteaApiResponse.success(List.of());
        }
        // 获取权限列表
        MPJLambdaWrapper<Permission> permissionWrapper=new MPJLambdaWrapper<Permission>()
                .selectAsClass(Permission.class,PermissionModel.class)
                .in(Permission::getId,permissionIds);
        List<PermissionModel> permissions = permissionMapper.selectJoinList(PermissionModel.class, permissionWrapper);
        return DexteaApiResponse.success(permissions);
    }
}

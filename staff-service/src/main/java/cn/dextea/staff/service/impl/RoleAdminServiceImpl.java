package cn.dextea.staff.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.converter.RoleConverter;
import cn.dextea.staff.dto.request.BindRolePermissionRequest;
import cn.dextea.staff.dto.request.CreateRoleRequest;
import cn.dextea.staff.dto.request.RolePageQueryRequest;
import cn.dextea.staff.dto.request.UpdateRoleRequest;
import cn.dextea.staff.dto.response.CreateRoleResponse;
import cn.dextea.staff.dto.response.RoleDetailResponse;
import cn.dextea.staff.entity.PermissionEntity;
import cn.dextea.staff.entity.RoleEntity;
import cn.dextea.staff.entity.RolePermissionRelEntity;
import cn.dextea.staff.enums.RoleErrorCode;
import cn.dextea.staff.enums.RoleStatus;
import cn.dextea.staff.mapper.PermissionMapper;
import cn.dextea.staff.mapper.RoleMapper;
import cn.dextea.staff.mapper.RolePermissionRelMapper;
import cn.dextea.staff.service.RoleAdminService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleAdminServiceImpl implements RoleAdminService {
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final RolePermissionRelMapper rolePermissionRelMapper;
    private final RoleConverter roleConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CreateRoleResponse> createRole(CreateRoleRequest request) {
        String name = request.getName().trim();
        String remark = trim(request.getRemark());

        if (existsByName(name, null)) {
            return fail(RoleErrorCode.ROLE_NAME_ALREADY_EXISTS);
        }

        RoleEntity roleEntity = RoleEntity.builder()
                .name(name)
                .remark(remark)
                .dataScope(request.getDataScope())
                .status(RoleStatus.AVAILABLE.getValue())
                .build();

        if (roleMapper.insert(roleEntity) != 1) {
            return fail(RoleErrorCode.CREATE_FAILED);
        }

        return ApiResponse.success(roleConverter.toCreateRoleResponse(roleEntity));
    }

    @Override
    public ApiResponse<IPage<RoleDetailResponse>> getRolePage(RolePageQueryRequest request) {
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<RoleEntity>()
                .like(hasText(request.getName()), RoleEntity::getName, trim(request.getName()))
                .eq(request.getDataScope() != null, RoleEntity::getDataScope, request.getDataScope())
                .eq(request.getStatus() != null, RoleEntity::getStatus, request.getStatus())
                .orderByDesc(RoleEntity::getId);

        IPage<RoleEntity> entityPage = roleMapper.selectPage(new Page<>(request.getCurrent(), request.getSize()), queryWrapper);
        IPage<RoleDetailResponse> responsePage = entityPage.convert(roleConverter::toRoleDetailResponse);
        return ApiResponse.success(responsePage);
    }

    @Override
    public ApiResponse<RoleDetailResponse> getRoleDetail(Long id) {
        RoleEntity roleEntity = roleMapper.selectById(id);
        if (roleEntity == null) {
            return fail(RoleErrorCode.ROLE_NOT_FOUND);
        }
        return ApiResponse.success(roleConverter.toRoleDetailResponse(roleEntity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<RoleDetailResponse> updateRole(Long id, UpdateRoleRequest request) {
        RoleEntity currentRole = roleMapper.selectById(id);
        if (currentRole == null) {
            return fail(RoleErrorCode.ROLE_NOT_FOUND);
        }

        String name = request.getName().trim();
        String remark = trim(request.getRemark());

        if (existsByName(name, id)) {
            return fail(RoleErrorCode.ROLE_NAME_ALREADY_EXISTS);
        }

        currentRole.setName(name);
        currentRole.setRemark(remark);
        currentRole.setDataScope(request.getDataScope());
        currentRole.setStatus(request.getStatus());

        if (roleMapper.updateById(currentRole) != 1) {
            return fail(RoleErrorCode.UPDATE_FAILED);
        }

        RoleEntity refreshedRole = roleMapper.selectById(id);
        return ApiResponse.success(roleConverter.toRoleDetailResponse(refreshedRole));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> deleteRole(Long id) {
        RoleEntity roleEntity = roleMapper.selectById(id);
        if (roleEntity == null) {
            return fail(RoleErrorCode.ROLE_NOT_FOUND);
        }

        roleEntity.setStatus(RoleStatus.DISABLED.getValue());
        if (roleMapper.updateById(roleEntity) != 1) {
            return fail(RoleErrorCode.UPDATE_FAILED);
        }

        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> bindPermission(Long id, BindRolePermissionRequest request) {
        RoleEntity roleEntity = roleMapper.selectById(id);
        if (roleEntity == null) {
            return fail(RoleErrorCode.ROLE_NOT_FOUND);
        }

        String permissionName = request.getPermissionName().trim();
        PermissionEntity permissionEntity = permissionMapper.selectOne(
                new LambdaQueryWrapper<PermissionEntity>().eq(PermissionEntity::getName, permissionName)
        );
        if (permissionEntity == null) {
            return fail(RoleErrorCode.PERMISSION_NOT_FOUND);
        }

        LambdaQueryWrapper<RolePermissionRelEntity> queryWrapper = new LambdaQueryWrapper<RolePermissionRelEntity>()
                .eq(RolePermissionRelEntity::getRoleId, id)
                .eq(RolePermissionRelEntity::getPermissionName, permissionName);
        if (rolePermissionRelMapper.exists(queryWrapper)) {
            return fail(RoleErrorCode.ROLE_PERMISSION_ALREADY_BOUND);
        }

        RolePermissionRelEntity relation = RolePermissionRelEntity.builder()
                .roleId(id)
                .permissionName(permissionName)
                .build();
        if (rolePermissionRelMapper.insert(relation) != 1) {
            return fail(RoleErrorCode.BIND_PERMISSION_FAILED);
        }

        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> unbindPermission(Long id, String permissionName) {
        RoleEntity roleEntity = roleMapper.selectById(id);
        if (roleEntity == null) {
            return fail(RoleErrorCode.ROLE_NOT_FOUND);
        }

        String trimmedPermissionName = permissionName.trim();
        PermissionEntity permissionEntity = permissionMapper.selectOne(
                new LambdaQueryWrapper<PermissionEntity>().eq(PermissionEntity::getName, trimmedPermissionName)
        );
        if (permissionEntity == null) {
            return fail(RoleErrorCode.PERMISSION_NOT_FOUND);
        }

        LambdaQueryWrapper<RolePermissionRelEntity> queryWrapper = new LambdaQueryWrapper<RolePermissionRelEntity>()
                .eq(RolePermissionRelEntity::getRoleId, id)
                .eq(RolePermissionRelEntity::getPermissionName, trimmedPermissionName);
        if (!rolePermissionRelMapper.exists(queryWrapper)) {
            return fail(RoleErrorCode.ROLE_PERMISSION_REL_NOT_FOUND);
        }

        if (rolePermissionRelMapper.delete(queryWrapper) != 1) {
            return fail(RoleErrorCode.UNBIND_PERMISSION_FAILED);
        }

        return ApiResponse.success();
    }

    private boolean existsByName(String name, Long excludeId) {
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getName, name)
                .ne(excludeId != null, RoleEntity::getId, excludeId);
        return roleMapper.exists(queryWrapper);
    }

    private <T> ApiResponse<T> fail(RoleErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}

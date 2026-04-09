package cn.dextea.staff.service.impl;

import cn.dextea.common.util.StringValueUtils;
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
    public ApiResponse<CreateRoleResponse> create(CreateRoleRequest request) {
        // 先规整字符串字段，避免前后空格影响唯一性判断和持久化结果。
        String name = request.getName().trim();
        String remark = StringValueUtils.trim(request.getRemark());

        // 角色名称必须全局唯一，创建前先做重名校验。
        if (existsByName(name, null)) {
            return fail(RoleErrorCode.ROLE_NAME_ALREADY_EXISTS);
        }

        // 组装待落库的角色实体，新增角色默认置为可用状态。
        RoleEntity roleEntity = RoleEntity.builder()
                .name(name)
                .remark(remark)
                .dataScope(request.getDataScope())
                .status(RoleStatus.AVAILABLE.getValue())
                .build();

        // 写入角色主表，失败则回滚整个事务。
        if (roleMapper.insert(roleEntity) != 1) {
            return fail(RoleErrorCode.CREATE_FAILED);
        }

        // 返回新建成功后的角色基础信息。
        return ApiResponse.success(roleConverter.toCreateRoleResponse(roleEntity));
    }

    @Override
    public ApiResponse<IPage<RoleDetailResponse>> page(RolePageQueryRequest request) {
        // 按名称、数据范围、状态动态拼装分页查询条件。
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<RoleEntity>()
                .like(StringValueUtils.hasText(request.getName()), RoleEntity::getName, StringValueUtils.trim(request.getName()))
                .eq(request.getDataScope() != null, RoleEntity::getDataScope, request.getDataScope())
                .eq(request.getStatus() != null, RoleEntity::getStatus, request.getStatus())
                .orderByDesc(RoleEntity::getId);

        // 查询分页实体并转换为详情分页结果。
        IPage<RoleEntity> entityPage = roleMapper.selectPage(new Page<>(request.getCurrent(), request.getSize()), queryWrapper);
        IPage<RoleDetailResponse> responsePage = entityPage.convert(roleConverter::toRoleDetailResponse);
        return ApiResponse.success(responsePage);
    }

    @Override
    public ApiResponse<RoleDetailResponse> detail(Long id) {
        // 先确认角色存在，再返回详情。
        RoleEntity roleEntity = roleMapper.selectById(id);
        if (roleEntity == null) {
            return fail(RoleErrorCode.ROLE_NOT_FOUND);
        }
        return ApiResponse.success(roleConverter.toRoleDetailResponse(roleEntity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<RoleDetailResponse> update(Long id, UpdateRoleRequest request) {
        // 更新前先查询当前角色，避免对不存在的数据执行更新。
        RoleEntity currentRole = roleMapper.selectById(id);
        if (currentRole == null) {
            return fail(RoleErrorCode.ROLE_NOT_FOUND);
        }

        // 规整可编辑字段，保证后续校验和存储使用统一值。
        String name = request.getName().trim();
        String remark = StringValueUtils.trim(request.getRemark());

        // 更新时排除自身后做重名校验，防止角色名称冲突。
        if (existsByName(name, id)) {
            return fail(RoleErrorCode.ROLE_NAME_ALREADY_EXISTS);
        }

        // 将请求中的最新字段回写到当前角色实体。
        currentRole.setName(name);
        currentRole.setRemark(remark);
        currentRole.setDataScope(request.getDataScope());
        currentRole.setStatus(request.getStatus());

        // 更新主表后再回查一次，确保响应里带的是最新数据库状态。
        if (roleMapper.updateById(currentRole) != 1) {
            return fail(RoleErrorCode.UPDATE_FAILED);
        }

        RoleEntity refreshedRole = roleMapper.selectById(id);
        return ApiResponse.success(roleConverter.toRoleDetailResponse(refreshedRole));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> delete(Long id) {
        // 删除角色采用软删除：先校验存在，再改为禁用状态。
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
        // 先确认角色存在，避免绑定到无效角色。
        RoleEntity roleEntity = roleMapper.selectById(id);
        if (roleEntity == null) {
            return fail(RoleErrorCode.ROLE_NOT_FOUND);
        }

        // 按权限名称查询权限定义，绑定关系必须指向真实存在的权限。
        String permissionName = request.getPermissionName().trim();
        PermissionEntity permissionEntity = permissionMapper.selectOne(
                new LambdaQueryWrapper<PermissionEntity>().eq(PermissionEntity::getName, permissionName)
        );
        if (permissionEntity == null) {
            return fail(RoleErrorCode.PERMISSION_NOT_FOUND);
        }

        // 避免重复插入同一角色与权限的关联关系。
        LambdaQueryWrapper<RolePermissionRelEntity> queryWrapper = new LambdaQueryWrapper<RolePermissionRelEntity>()
                .eq(RolePermissionRelEntity::getRoleId, id)
                .eq(RolePermissionRelEntity::getPermissionName, permissionName);
        if (rolePermissionRelMapper.exists(queryWrapper)) {
            return fail(RoleErrorCode.ROLE_PERMISSION_ALREADY_BOUND);
        }

        // 创建角色-权限关系记录。
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
        // 先确认角色存在，再处理角色权限解绑。
        RoleEntity roleEntity = roleMapper.selectById(id);
        if (roleEntity == null) {
            return fail(RoleErrorCode.ROLE_NOT_FOUND);
        }

        // 根据权限名称确认目标权限存在，避免传入脏数据。
        String trimmedPermissionName = permissionName.trim();
        PermissionEntity permissionEntity = permissionMapper.selectOne(
                new LambdaQueryWrapper<PermissionEntity>().eq(PermissionEntity::getName, trimmedPermissionName)
        );
        if (permissionEntity == null) {
            return fail(RoleErrorCode.PERMISSION_NOT_FOUND);
        }

        // 先判断关系是否存在，避免删除不存在的绑定记录。
        LambdaQueryWrapper<RolePermissionRelEntity> queryWrapper = new LambdaQueryWrapper<RolePermissionRelEntity>()
                .eq(RolePermissionRelEntity::getRoleId, id)
                .eq(RolePermissionRelEntity::getPermissionName, trimmedPermissionName);
        if (!rolePermissionRelMapper.exists(queryWrapper)) {
            return fail(RoleErrorCode.ROLE_PERMISSION_REL_NOT_FOUND);
        }

        // 删除角色-权限关系。
        if (rolePermissionRelMapper.delete(queryWrapper) != 1) {
            return fail(RoleErrorCode.UNBIND_PERMISSION_FAILED);
        }

        return ApiResponse.success();
    }

    private boolean existsByName(String name, Long excludeId) {
        // 通用重名校验：更新场景下允许排除当前角色自身。
        LambdaQueryWrapper<RoleEntity> queryWrapper = new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getName, name)
                .ne(excludeId != null, RoleEntity::getId, excludeId);
        return roleMapper.exists(queryWrapper);
    }

    private <T> ApiResponse<T> fail(RoleErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}

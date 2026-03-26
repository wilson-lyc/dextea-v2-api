package cn.dextea.staff.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.converter.StaffConverter;
import cn.dextea.staff.dto.request.AssignStaffRoleRequest;
import cn.dextea.staff.dto.request.CreateStaffRequest;
import cn.dextea.staff.dto.request.StaffPageQueryRequest;
import cn.dextea.staff.dto.request.UpdateStaffRequest;
import cn.dextea.staff.dto.response.CreateStaffResponse;
import cn.dextea.staff.dto.response.ResetStaffPasswordResponse;
import cn.dextea.staff.dto.response.StaffDetailResponse;
import cn.dextea.staff.entity.RoleEntity;
import cn.dextea.staff.entity.StaffEntity;
import cn.dextea.staff.entity.StaffRoleRelEntity;
import cn.dextea.staff.enums.StaffErrorCode;
import cn.dextea.staff.enums.StaffStatus;
import cn.dextea.staff.mapper.RoleMapper;
import cn.dextea.staff.mapper.StaffMapper;
import cn.dextea.staff.mapper.StaffRoleRelMapper;
import cn.dextea.staff.service.StaffAdminService;
import cn.dextea.staff.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StaffAdminServiceImpl implements StaffAdminService {
    private final StaffMapper staffMapper;
    private final RoleMapper roleMapper;
    private final StaffRoleRelMapper staffRoleRelMapper;
    private final PasswordUtil passwordUtil;
    private final StaffConverter staffConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CreateStaffResponse> createStaff(CreateStaffRequest request) {
        String username = request.getUsername().trim();
        String realName = request.getRealName().trim();

        if (existsByUsername(username, null)) {
            return fail(StaffErrorCode.USERNAME_ALREADY_EXISTS);
        }

        String initialPassword = passwordUtil.generate();
        StaffEntity staffEntity = StaffEntity.builder()
                .username(username)
                .realName(realName)
                .userType(request.getUserType())
                .status(StaffStatus.AVAILABLE.getValue())
                .password(passwordUtil.encode(initialPassword))
                .build();

        if (staffMapper.insert(staffEntity) != 1) {
            return fail(StaffErrorCode.CREATE_FAILED);
        }

        return ApiResponse.success(staffConverter.toCreateStaffResponse(staffEntity, initialPassword));
    }

    @Override
    public ApiResponse<IPage<StaffDetailResponse>> getStaffPage(StaffPageQueryRequest request) {
        LambdaQueryWrapper<StaffEntity> queryWrapper = new LambdaQueryWrapper<StaffEntity>()
                .like(hasText(request.getUsername()), StaffEntity::getUsername, trim(request.getUsername()))
                .like(hasText(request.getRealName()), StaffEntity::getRealName, trim(request.getRealName()))
                .eq(request.getUserType() != null, StaffEntity::getUserType, request.getUserType())
                .eq(request.getStatus() != null, StaffEntity::getStatus, request.getStatus())
                .orderByDesc(StaffEntity::getId);

        IPage<StaffEntity> entityPage = staffMapper.selectPage(new Page<>(request.getCurrent(), request.getSize()), queryWrapper);
        IPage<StaffDetailResponse> responsePage = entityPage.convert(staffConverter::toStaffDetailResponse);
        return ApiResponse.success(responsePage);
    }

    @Override
    public ApiResponse<StaffDetailResponse> getStaffDetail(Long id) {
        StaffEntity staffEntity = staffMapper.selectById(id);
        if (staffEntity == null) {
            return fail(StaffErrorCode.STAFF_NOT_FOUND);
        }
        return ApiResponse.success(staffConverter.toStaffDetailResponse(staffEntity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<StaffDetailResponse> updateStaff(Long id, UpdateStaffRequest request) {
        StaffEntity currentStaff = staffMapper.selectById(id);
        if (currentStaff == null) {
            return fail(StaffErrorCode.STAFF_NOT_FOUND);
        }

        String username = request.getUsername().trim();
        String realName = request.getRealName().trim();

        if (existsByUsername(username, id)) {
            return fail(StaffErrorCode.USERNAME_ALREADY_EXISTS);
        }

        currentStaff.setUsername(username);
        currentStaff.setRealName(realName);
        currentStaff.setUserType(request.getUserType());
        currentStaff.setStatus(request.getStatus());

        if (staffMapper.updateById(currentStaff) != 1) {
            return fail(StaffErrorCode.UPDATE_FAILED);
        }

        StaffEntity refreshedStaff = staffMapper.selectById(id);
        return ApiResponse.success(staffConverter.toStaffDetailResponse(refreshedStaff));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> deleteStaff(Long id) {
        StaffEntity staffEntity = staffMapper.selectById(id);
        if (staffEntity == null) {
            return fail(StaffErrorCode.STAFF_NOT_FOUND);
        }

        staffEntity.setStatus(StaffStatus.DISABLED.getValue());
        if (staffMapper.updateById(staffEntity) != 1) {
            return fail(StaffErrorCode.UPDATE_FAILED);
        }

        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<ResetStaffPasswordResponse> resetPassword(Long id) {
        StaffEntity staffEntity = staffMapper.selectById(id);
        if (staffEntity == null) {
            return fail(StaffErrorCode.STAFF_NOT_FOUND);
        }

        String resetPassword = passwordUtil.generate();
        staffEntity.setPassword(passwordUtil.encode(resetPassword));

        if (staffMapper.updateById(staffEntity) != 1) {
            return fail(StaffErrorCode.RESET_PASSWORD_FAILED);
        }

        return ApiResponse.success(staffConverter.toResetPasswordResponse(staffEntity, resetPassword));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> assignRole(Long id, AssignStaffRoleRequest request) {
        StaffEntity staffEntity = staffMapper.selectById(id);
        if (staffEntity == null) {
            return fail(StaffErrorCode.STAFF_NOT_FOUND);
        }

        RoleEntity roleEntity = roleMapper.selectById(request.getRoleId());
        if (roleEntity == null) {
            return fail(StaffErrorCode.ROLE_NOT_FOUND);
        }

        LambdaQueryWrapper<StaffRoleRelEntity> queryWrapper = new LambdaQueryWrapper<StaffRoleRelEntity>()
                .eq(StaffRoleRelEntity::getStaffId, id)
                .eq(StaffRoleRelEntity::getRoleId, request.getRoleId());
        if (staffRoleRelMapper.exists(queryWrapper)) {
            return fail(StaffErrorCode.STAFF_ROLE_ALREADY_BOUND);
        }

        StaffRoleRelEntity relation = StaffRoleRelEntity.builder()
                .staffId(id)
                .roleId(request.getRoleId())
                .build();
        if (staffRoleRelMapper.insert(relation) != 1) {
            return fail(StaffErrorCode.ASSIGN_ROLE_FAILED);
        }

        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> unbindRole(Long id, Long roleId) {
        StaffEntity staffEntity = staffMapper.selectById(id);
        if (staffEntity == null) {
            return fail(StaffErrorCode.STAFF_NOT_FOUND);
        }

        RoleEntity roleEntity = roleMapper.selectById(roleId);
        if (roleEntity == null) {
            return fail(StaffErrorCode.ROLE_NOT_FOUND);
        }

        LambdaQueryWrapper<StaffRoleRelEntity> queryWrapper = new LambdaQueryWrapper<StaffRoleRelEntity>()
                .eq(StaffRoleRelEntity::getStaffId, id)
                .eq(StaffRoleRelEntity::getRoleId, roleId);
        if (!staffRoleRelMapper.exists(queryWrapper)) {
            return fail(StaffErrorCode.STAFF_ROLE_REL_NOT_FOUND);
        }

        if (staffRoleRelMapper.delete(queryWrapper) != 1) {
            return fail(StaffErrorCode.UNBIND_ROLE_FAILED);
        }

        return ApiResponse.success();
    }

    private boolean existsByUsername(String username, Long excludeId) {
        LambdaQueryWrapper<StaffEntity> queryWrapper = new LambdaQueryWrapper<StaffEntity>()
                .eq(StaffEntity::getUsername, username)
                .ne(excludeId != null, StaffEntity::getId, excludeId);
        return staffMapper.exists(queryWrapper);
    }

    private <T> ApiResponse<T> fail(StaffErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}

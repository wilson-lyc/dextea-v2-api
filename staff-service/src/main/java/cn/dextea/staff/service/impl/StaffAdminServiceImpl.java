package cn.dextea.staff.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.store.api.dto.response.StoreValidityResponse;
import cn.dextea.store.api.feign.StoreInternalFeign;
import cn.dextea.staff.converter.StaffConverter;
import cn.dextea.staff.dto.request.AssignStaffRoleRequest;
import cn.dextea.staff.dto.request.BindStaffStoreRequest;
import cn.dextea.staff.dto.request.CreateStaffRequest;
import cn.dextea.staff.dto.request.StaffPageQueryRequest;
import cn.dextea.staff.dto.request.UpdateStaffRequest;
import cn.dextea.staff.dto.response.CreateStaffResponse;
import cn.dextea.staff.dto.response.ResetStaffPasswordResponse;
import cn.dextea.staff.dto.response.StaffDetailResponse;
import cn.dextea.staff.entity.RoleEntity;
import cn.dextea.staff.entity.StaffEntity;
import cn.dextea.staff.entity.StaffRoleRelEntity;
import cn.dextea.staff.entity.StaffStoreRelEntity;
import cn.dextea.staff.enums.StaffErrorCode;
import cn.dextea.staff.enums.StaffStatus;
import cn.dextea.staff.enums.StaffType;
import cn.dextea.staff.mapper.RoleMapper;
import cn.dextea.staff.mapper.StaffMapper;
import cn.dextea.staff.mapper.StaffRoleRelMapper;
import cn.dextea.staff.mapper.StaffStoreRelMapper;
import cn.dextea.staff.service.StaffAdminService;
import cn.dextea.staff.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StaffAdminServiceImpl implements StaffAdminService {
    private final StaffMapper staffMapper;
    private final RoleMapper roleMapper;
    private final StaffRoleRelMapper staffRoleRelMapper;
    private final StaffStoreRelMapper staffStoreRelMapper;
    private final PasswordUtil passwordUtil;
    private final StaffConverter staffConverter;
    private final StoreInternalFeign storeInternalFeign;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CreateStaffResponse> createStaff(CreateStaffRequest request) {
        // 创建前先校验员工类型是否合法，避免非法字典值落库。
        if (!StaffType.isValid(request.getUserType())) {
            return fail(StaffErrorCode.INVALID_USER_TYPE);
        }

        // 先规整账号和姓名，避免空格导致的重复账号问题。
        String username = request.getUsername().trim();
        String realName = request.getRealName().trim();

        // 员工账号必须唯一，创建前先做冲突校验。
        if (existsByUsername(username, null)) {
            return fail(StaffErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // 生成初始密码并保存加密后的密码摘要，明文只在响应中返回一次。
        String initialPassword = passwordUtil.generate();
        StaffEntity staffEntity = StaffEntity.builder()
                .username(username)
                .realName(realName)
                .userType(request.getUserType())
                .status(StaffStatus.AVAILABLE.getValue())
                .password(passwordUtil.encode(initialPassword))
                .build();

        // 新增员工主记录。
        if (staffMapper.insert(staffEntity) != 1) {
            return fail(StaffErrorCode.CREATE_FAILED);
        }

        // 返回员工基础信息以及系统生成的初始密码。
        return ApiResponse.success(staffConverter.toCreateStaffResponse(staffEntity, initialPassword));
    }

    @Override
    public ApiResponse<IPage<StaffDetailResponse>> getStaffPage(StaffPageQueryRequest request) {
        // 分页筛选中的员工类型和状态如果传了值，也必须满足字典约束。
        if (request.getUserType() != null && !StaffType.isValid(request.getUserType())) {
            return fail(StaffErrorCode.INVALID_USER_TYPE);
        }
        if (request.getStatus() != null && !StaffStatus.isValid(request.getStatus())) {
            return fail(StaffErrorCode.INVALID_STATUS);
        }

        // 按账号、姓名、用户类型、状态动态构建分页查询条件。
        LambdaQueryWrapper<StaffEntity> queryWrapper = new LambdaQueryWrapper<StaffEntity>()
                .like(hasText(request.getUsername()), StaffEntity::getUsername, trim(request.getUsername()))
                .like(hasText(request.getRealName()), StaffEntity::getRealName, trim(request.getRealName()))
                .eq(request.getUserType() != null, StaffEntity::getUserType, request.getUserType())
                .eq(request.getStatus() != null, StaffEntity::getStatus, request.getStatus())
                .orderByDesc(StaffEntity::getId);

        if (request.getStoreId() != null) {
            if (!isStoreValid(request.getStoreId())) {
                return fail(StaffErrorCode.STORE_NOT_FOUND);
            }
            // 仅在指定门店时追加子查询过滤，未指定时保持原有单表分页查询。
            queryWrapper.inSql(StaffEntity::getId,
                    "select distinct staff_id from staff_store_rel where store_id = " + request.getStoreId());
        }

        // 查询员工分页数据并转换成接口响应模型。
        IPage<StaffEntity> entityPage = staffMapper.selectPage(new Page<>(request.getCurrent(), request.getSize()), queryWrapper);
        IPage<StaffDetailResponse> responsePage = entityPage.convert(staffConverter::toStaffDetailResponse);
        return ApiResponse.success(responsePage);
    }

    @Override
    public ApiResponse<StaffDetailResponse> getStaffDetail(Long id) {
        // 根据员工主键查询详情，不存在则返回业务错误。
        StaffEntity staffEntity = staffMapper.selectById(id);
        if (staffEntity == null) {
            return fail(StaffErrorCode.STAFF_NOT_FOUND);
        }
        return ApiResponse.success(staffConverter.toStaffDetailResponse(staffEntity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<StaffDetailResponse> updateStaff(Long id, UpdateStaffRequest request) {
        // 更新前先校验员工类型和状态是否合法。
        if (!StaffType.isValid(request.getUserType())) {
            return fail(StaffErrorCode.INVALID_USER_TYPE);
        }
        if (!StaffStatus.isValid(request.getStatus())) {
            return fail(StaffErrorCode.INVALID_STATUS);
        }

        // 更新前先确认员工存在。
        StaffEntity currentStaff = staffMapper.selectById(id);
        if (currentStaff == null) {
            return fail(StaffErrorCode.STAFF_NOT_FOUND);
        }

        // 规整可编辑字段，避免空格影响唯一性和展示。
        String username = request.getUsername().trim();
        String realName = request.getRealName().trim();

        // 更新账号时排除自身后做重名校验。
        if (existsByUsername(username, id)) {
            return fail(StaffErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // 将最新请求字段回写到当前员工实体。
        currentStaff.setUsername(username);
        currentStaff.setRealName(realName);
        currentStaff.setUserType(request.getUserType());
        currentStaff.setStatus(request.getStatus());

        // 持久化后回查最新员工详情返回给调用方。
        if (staffMapper.updateById(currentStaff) != 1) {
            return fail(StaffErrorCode.UPDATE_FAILED);
        }

        StaffEntity refreshedStaff = staffMapper.selectById(id);
        return ApiResponse.success(staffConverter.toStaffDetailResponse(refreshedStaff));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> deleteStaff(Long id) {
        // 删除员工采用软删除：将账号状态改为禁用。
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
    public ApiResponse<Void> enableStaff(Long id) {
        // 激活员工：将账号状态改为可用。
        StaffEntity staffEntity = staffMapper.selectById(id);
        if (staffEntity == null) {
            return fail(StaffErrorCode.STAFF_NOT_FOUND);
        }

        staffEntity.setStatus(StaffStatus.AVAILABLE.getValue());
        if (staffMapper.updateById(staffEntity) != 1) {
            return fail(StaffErrorCode.UPDATE_FAILED);
        }

        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<ResetStaffPasswordResponse> resetPassword(Long id) {
        // 先确认目标员工存在。
        StaffEntity staffEntity = staffMapper.selectById(id);
        if (staffEntity == null) {
            return fail(StaffErrorCode.STAFF_NOT_FOUND);
        }

        // 生成新的随机密码，并将加密结果写回数据库。
        String resetPassword = passwordUtil.generate();
        staffEntity.setPassword(passwordUtil.encode(resetPassword));

        if (staffMapper.updateById(staffEntity) != 1) {
            return fail(StaffErrorCode.RESET_PASSWORD_FAILED);
        }

        // 把重置后的明文密码回传给管理员。
        return ApiResponse.success(staffConverter.toResetPasswordResponse(staffEntity, resetPassword));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> assignRole(Long id, AssignStaffRoleRequest request) {
        // 先确认员工和角色都真实存在，避免产生脏关联数据。
        StaffEntity staffEntity = staffMapper.selectById(id);
        if (staffEntity == null) {
            return fail(StaffErrorCode.STAFF_NOT_FOUND);
        }

        RoleEntity roleEntity = roleMapper.selectById(request.getRoleId());
        if (roleEntity == null) {
            return fail(StaffErrorCode.ROLE_NOT_FOUND);
        }

        // 如果该员工已经绑定过这个角色，则直接返回重复绑定错误。
        LambdaQueryWrapper<StaffRoleRelEntity> queryWrapper = new LambdaQueryWrapper<StaffRoleRelEntity>()
                .eq(StaffRoleRelEntity::getStaffId, id)
                .eq(StaffRoleRelEntity::getRoleId, request.getRoleId());
        if (staffRoleRelMapper.exists(queryWrapper)) {
            return fail(StaffErrorCode.STAFF_ROLE_ALREADY_BOUND);
        }

        // 写入员工-角色关联表。
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
        // 先确认员工和角色存在，再校验解绑关系。
        StaffEntity staffEntity = staffMapper.selectById(id);
        if (staffEntity == null) {
            return fail(StaffErrorCode.STAFF_NOT_FOUND);
        }

        RoleEntity roleEntity = roleMapper.selectById(roleId);
        if (roleEntity == null) {
            return fail(StaffErrorCode.ROLE_NOT_FOUND);
        }

        // 如果关联关系不存在，说明该员工本来就没有绑定这个角色。
        LambdaQueryWrapper<StaffRoleRelEntity> queryWrapper = new LambdaQueryWrapper<StaffRoleRelEntity>()
                .eq(StaffRoleRelEntity::getStaffId, id)
                .eq(StaffRoleRelEntity::getRoleId, roleId);
        if (!staffRoleRelMapper.exists(queryWrapper)) {
            return fail(StaffErrorCode.STAFF_ROLE_REL_NOT_FOUND);
        }

        // 删除员工-角色关联记录。
        if (staffRoleRelMapper.delete(queryWrapper) != 1) {
            return fail(StaffErrorCode.UNBIND_ROLE_FAILED);
        }

        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> bindStore(Long id, BindStaffStoreRequest request) {
        // 先确认员工存在且为门店员工，再校验目标门店是否合法。
        StaffEntity staffEntity = staffMapper.selectById(id);
        if (staffEntity == null) {
            return fail(StaffErrorCode.STAFF_NOT_FOUND);
        }
        if (!Objects.equals(staffEntity.getUserType(), StaffType.STORE.getValue())) {
            return fail(StaffErrorCode.STORE_BIND_ONLY_FOR_STORE_STAFF);
        }
        if (!isStoreValid(request.getStoreId())) {
            return fail(StaffErrorCode.STORE_NOT_FOUND);
        }

        // 门店绑定关系按独立记录存储：存在即视为已绑定，不存在则插入一条新关系。
        LambdaQueryWrapper<StaffStoreRelEntity> queryWrapper = new LambdaQueryWrapper<StaffStoreRelEntity>()
                .eq(StaffStoreRelEntity::getStaffId, id)
                .eq(StaffStoreRelEntity::getStoreId, request.getStoreId());
        if (staffStoreRelMapper.exists(queryWrapper)) {
            return fail(StaffErrorCode.STAFF_STORE_ALREADY_BOUND);
        }

        StaffStoreRelEntity relation = StaffStoreRelEntity.builder()
                .staffId(id)
                .storeId(request.getStoreId())
                .build();
        if (staffStoreRelMapper.insert(relation) != 1) {
            return fail(StaffErrorCode.BIND_STORE_FAILED);
        }

        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> unbindStore(Long id) {
        // 仅门店员工支持执行门店解绑，避免公司员工出现无意义操作。
        StaffEntity staffEntity = staffMapper.selectById(id);
        if (staffEntity == null) {
            return fail(StaffErrorCode.STAFF_NOT_FOUND);
        }
        if (!Objects.equals(staffEntity.getUserType(), StaffType.STORE.getValue())) {
            return fail(StaffErrorCode.STORE_BIND_ONLY_FOR_STORE_STAFF);
        }

        LambdaQueryWrapper<StaffStoreRelEntity> queryWrapper = new LambdaQueryWrapper<StaffStoreRelEntity>()
                .eq(StaffStoreRelEntity::getStaffId, id);
        if (!staffStoreRelMapper.exists(queryWrapper)) {
            return fail(StaffErrorCode.STAFF_STORE_REL_NOT_FOUND);
        }

        // 解绑门店时直接删除员工-门店关系记录，不改动员工-角色关系。
        if (staffStoreRelMapper.delete(queryWrapper) < 1) {
            return fail(StaffErrorCode.UNBIND_STORE_FAILED);
        }

        return ApiResponse.success();
    }

    private boolean existsByUsername(String username, Long excludeId) {
        // 通用账号重名校验：更新场景下排除当前员工自身。
        LambdaQueryWrapper<StaffEntity> queryWrapper = new LambdaQueryWrapper<StaffEntity>()
                .eq(StaffEntity::getUsername, username)
                .ne(excludeId != null, StaffEntity::getId, excludeId);
        return staffMapper.exists(queryWrapper);
    }

    private boolean isStoreValid(Long storeId) {
        ApiResponse<StoreValidityResponse> response = storeInternalFeign.checkStoreValidity(storeId);
        return response != null
                && response.getCode() != null
                && response.getCode() == 0
                && response.getData() != null
                && Boolean.TRUE.equals(response.getData().getValid());
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

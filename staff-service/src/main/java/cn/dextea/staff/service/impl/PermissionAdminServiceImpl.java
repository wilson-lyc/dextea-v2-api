package cn.dextea.staff.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.converter.PermissionConverter;
import cn.dextea.staff.dto.request.PermissionPageQueryRequest;
import cn.dextea.staff.dto.response.PermissionDetailResponse;
import cn.dextea.staff.entity.PermissionEntity;
import cn.dextea.staff.enums.PermissionErrorCode;
import cn.dextea.staff.mapper.PermissionMapper;
import cn.dextea.staff.service.PermissionAdminService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionAdminServiceImpl implements PermissionAdminService {
    private final PermissionMapper permissionMapper;
    private final PermissionConverter permissionConverter;

    @Override
    public ApiResponse<IPage<PermissionDetailResponse>> getPermissionPage(PermissionPageQueryRequest request) {
        LambdaQueryWrapper<PermissionEntity> queryWrapper = new LambdaQueryWrapper<PermissionEntity>()
                .like(hasText(request.getName()), PermissionEntity::getName, trim(request.getName()))
                .orderByDesc(PermissionEntity::getId);

        IPage<PermissionEntity> entityPage = permissionMapper.selectPage(new Page<>(request.getCurrent(), request.getSize()), queryWrapper);
        IPage<PermissionDetailResponse> responsePage = entityPage.convert(permissionConverter::toPermissionDetailResponse);
        return ApiResponse.success(responsePage);
    }

    @Override
    public ApiResponse<PermissionDetailResponse> getPermissionDetail(Long id) {
        PermissionEntity permissionEntity = permissionMapper.selectById(id);
        if (permissionEntity == null) {
            return ApiResponse.fail(PermissionErrorCode.PERMISSION_NOT_FOUND.getCode(), PermissionErrorCode.PERMISSION_NOT_FOUND.getMsg());
        }
        return ApiResponse.success(permissionConverter.toPermissionDetailResponse(permissionEntity));
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}

package cn.dextea.staff.service.impl;

import cn.dextea.common.util.StringValueUtils;
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
    public ApiResponse<IPage<PermissionDetailResponse>> page(PermissionPageQueryRequest request) {
        // 按查询条件拼装分页检索语句，仅在传入名称时追加模糊过滤。
        LambdaQueryWrapper<PermissionEntity> queryWrapper = new LambdaQueryWrapper<PermissionEntity>()
                .like(StringValueUtils.hasText(request.getName()), PermissionEntity::getName, StringValueUtils.trim(request.getName()))
                .orderByDesc(PermissionEntity::getId);

        // 先查实体分页结果，再统一转换为接口响应对象。
        IPage<PermissionEntity> entityPage = permissionMapper.selectPage(new Page<>(request.getCurrent(), request.getSize()), queryWrapper);
        IPage<PermissionDetailResponse> responsePage = entityPage.convert(permissionConverter::toPermissionDetailResponse);
        return ApiResponse.success(responsePage);
    }

    @Override
    public ApiResponse<PermissionDetailResponse> detail(Long id) {
        // 根据主键查询权限详情，不存在时直接返回业务错误。
        PermissionEntity permissionEntity = permissionMapper.selectById(id);
        if (permissionEntity == null) {
            return ApiResponse.fail(PermissionErrorCode.PERMISSION_NOT_FOUND.getCode(), PermissionErrorCode.PERMISSION_NOT_FOUND.getMsg());
        }

        // 查询成功后转换为详情响应。
        return ApiResponse.success(permissionConverter.toPermissionDetailResponse(permissionEntity));
    }
}

package cn.dextea.product.service.impl;

import cn.dextea.common.util.StringValueUtils;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.dto.request.CreateCustomizationItemRequest;
import cn.dextea.product.dto.request.CustomizationItemPageRequest;
import cn.dextea.product.dto.request.UpdateCustomizationItemRequest;
import cn.dextea.product.dto.request.UpdateCustomizationItemStatusRequest;
import cn.dextea.product.dto.response.CreateCustomizationItemResponse;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.CustomizationStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.service.CustomizationItemAdminService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomizationItemAdminServiceImpl implements CustomizationItemAdminService {

    private final CustomizationItemMapper itemMapper;
    private final CustomizationConverter customizationConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CreateCustomizationItemResponse> create(CreateCustomizationItemRequest request) {
        String name = request.getName().trim();

        if (nameExists(name, null)) {
            return fail(CustomizationErrorCode.ITEM_NAME_DUPLICATE);
        }

        CustomizationItemEntity entity = CustomizationItemEntity.builder()
                .name(name)
                .description(request.getDescription())
                .status(CustomizationStatus.DISABLED.getValue())
                .build();

        if (itemMapper.insert(entity) != 1) {
            return fail(CustomizationErrorCode.ITEM_CREATE_FAILED);
        }
        return ApiResponse.success(customizationConverter.toCreateItemResponse(entity));
    }

    @Override
    public ApiResponse<IPage<CustomizationItemDetailResponse>> getPage(CustomizationItemPageRequest request) {
        LambdaQueryWrapper<CustomizationItemEntity> query = new LambdaQueryWrapper<CustomizationItemEntity>()
                .like(StringValueUtils.hasText(request.getName()),
                        CustomizationItemEntity::getName, request.getName())
                .eq(request.getStatus() != null,
                        CustomizationItemEntity::getStatus, request.getStatus())
                .orderByDesc(CustomizationItemEntity::getId);

        IPage<CustomizationItemEntity> entityPage = itemMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), query);

        return ApiResponse.success(entityPage.convert(customizationConverter::toItemDetailResponse));
    }

    @Override
    public ApiResponse<CustomizationItemDetailResponse> getDetail(Long id) {
        CustomizationItemEntity entity = itemMapper.selectById(id);
        if (entity == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }
        return ApiResponse.success(customizationConverter.toItemDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CustomizationItemDetailResponse> updateInfo(Long id, UpdateCustomizationItemRequest request) {
        CustomizationItemEntity entity = itemMapper.selectById(id);
        if (entity == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        String name = request.getName().trim();
        if (nameExists(name, id)) {
            return fail(CustomizationErrorCode.ITEM_NAME_DUPLICATE);
        }

        entity.setName(name);
        entity.setDescription(request.getDescription());
        if (itemMapper.updateById(entity) != 1) {
            return fail(CustomizationErrorCode.ITEM_UPDATE_FAILED);
        }

        return ApiResponse.success(customizationConverter.toItemDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> updateStatus(Long id, UpdateCustomizationItemStatusRequest request) {
        CustomizationItemEntity entity = itemMapper.selectById(id);
        if (entity == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        entity.setStatus(request.getStatus());
        if (itemMapper.updateById(entity) != 1) {
            return fail(CustomizationErrorCode.ITEM_UPDATE_FAILED);
        }

        return ApiResponse.success();
    }

    private boolean nameExists(String name, Long excludeId) {
        return itemMapper.exists(new LambdaQueryWrapper<CustomizationItemEntity>()
                .eq(CustomizationItemEntity::getName, name)
                .ne(CustomizationItemEntity::getStatus, CustomizationStatus.DISABLED.getValue())
                .ne(excludeId != null, CustomizationItemEntity::getId, excludeId));
    }

    private <T> ApiResponse<T> fail(CustomizationErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}

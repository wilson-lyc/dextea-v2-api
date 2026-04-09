package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.dto.request.CreateCustomizationItemRequest;
import cn.dextea.product.dto.request.CustomizationItemPageQueryRequest;
import cn.dextea.product.dto.request.UpdateCustomizationItemRequest;
import cn.dextea.product.dto.response.CreateCustomizationItemResponse;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.entity.CustomizationOptionEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.CustomizationStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.CustomizationOptionMapper;
import cn.dextea.product.service.CustomizationItemAdminService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomizationItemAdminServiceImpl implements CustomizationItemAdminService {

    private final CustomizationItemMapper itemMapper;
    private final CustomizationOptionMapper optionMapper;
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
                .status(CustomizationStatus.ACTIVE.getValue())
                .build();

        itemMapper.insert(entity);
        return ApiResponse.success(customizationConverter.toCreateItemResponse(entity));
    }

    @Override
    public ApiResponse<IPage<CustomizationItemDetailResponse>> page(CustomizationItemPageQueryRequest request) {
        LambdaQueryWrapper<CustomizationItemEntity> query = new LambdaQueryWrapper<CustomizationItemEntity>()
                .like(request.getName() != null && !request.getName().isBlank(),
                        CustomizationItemEntity::getName, request.getName())
                .eq(request.getStatus() != null, CustomizationItemEntity::getStatus, request.getStatus())
                .orderByDesc(CustomizationItemEntity::getId);

        IPage<CustomizationItemEntity> entityPage = itemMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), query);

        return ApiResponse.success(entityPage.convert(customizationConverter::toItemDetailResponse));
    }

    @Override
    public ApiResponse<CustomizationItemDetailResponse> detail(Long id) {
        CustomizationItemEntity entity = itemMapper.selectById(id);
        if (entity == null || CustomizationStatus.DISABLED.getValue().equals(entity.getStatus())) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        List<CustomizationOptionEntity> options = optionMapper.selectList(
                new LambdaQueryWrapper<CustomizationOptionEntity>()
                        .eq(CustomizationOptionEntity::getItemId, id)
                        .ne(CustomizationOptionEntity::getStatus, CustomizationStatus.DISABLED.getValue())
                        .orderByAsc(CustomizationOptionEntity::getId));

        List<CustomizationOptionDetailResponse> optionResponses = options.stream()
                .map(customizationConverter::toOptionDetailResponse)
                .collect(Collectors.toList());

        return ApiResponse.success(customizationConverter.toItemDetailResponse(entity, optionResponses));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CustomizationItemDetailResponse> update(Long id, UpdateCustomizationItemRequest request) {
        CustomizationItemEntity entity = itemMapper.selectById(id);
        if (entity == null || CustomizationStatus.DISABLED.getValue().equals(entity.getStatus())) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        String name = request.getName().trim();
        if (nameExists(name, id)) {
            return fail(CustomizationErrorCode.ITEM_NAME_DUPLICATE);
        }

        entity.setName(name);
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        itemMapper.updateById(entity);

        List<CustomizationOptionEntity> options = optionMapper.selectList(
                new LambdaQueryWrapper<CustomizationOptionEntity>()
                        .eq(CustomizationOptionEntity::getItemId, id)
                        .ne(CustomizationOptionEntity::getStatus, CustomizationStatus.DISABLED.getValue())
                        .orderByAsc(CustomizationOptionEntity::getId));

        List<CustomizationOptionDetailResponse> optionResponses = options.stream()
                .map(customizationConverter::toOptionDetailResponse)
                .collect(Collectors.toList());

        return ApiResponse.success(customizationConverter.toItemDetailResponse(entity, optionResponses));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> delete(Long id) {
        CustomizationItemEntity entity = itemMapper.selectById(id);
        if (entity == null || CustomizationStatus.DISABLED.getValue().equals(entity.getStatus())) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        // Soft-delete all active options under this item
        optionMapper.update(new LambdaUpdateWrapper<CustomizationOptionEntity>()
                .eq(CustomizationOptionEntity::getItemId, id)
                .ne(CustomizationOptionEntity::getStatus, CustomizationStatus.DISABLED.getValue())
                .set(CustomizationOptionEntity::getStatus, CustomizationStatus.DISABLED.getValue()));

        entity.setStatus(CustomizationStatus.DISABLED.getValue());
        itemMapper.updateById(entity);
        return ApiResponse.success();
    }

    // ---- Helpers ----

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

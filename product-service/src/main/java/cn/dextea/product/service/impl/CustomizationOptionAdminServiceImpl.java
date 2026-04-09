package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.dto.request.CreateCustomizationOptionRequest;
import cn.dextea.product.dto.request.UpdateCustomizationOptionRequest;
import cn.dextea.product.dto.response.CreateCustomizationOptionResponse;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.entity.CustomizationOptionEntity;
import cn.dextea.product.entity.IngredientEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.CustomizationStatus;
import cn.dextea.product.enums.IngredientStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.CustomizationOptionMapper;
import cn.dextea.product.mapper.IngredientMapper;
import cn.dextea.product.service.CustomizationOptionAdminService;
import cn.dextea.product.service.ProductCacheEvictionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomizationOptionAdminServiceImpl implements CustomizationOptionAdminService {

    private final CustomizationItemMapper itemMapper;
    private final CustomizationOptionMapper optionMapper;
    private final IngredientMapper ingredientMapper;
    private final CustomizationConverter customizationConverter;
    private final ProductCacheEvictionService cacheEvictionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CreateCustomizationOptionResponse> createOption(Long itemId,
            CreateCustomizationOptionRequest request) {
        CustomizationItemEntity item = getActiveItemById(itemId);
        if (item == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        String name = request.getName().trim();
        if (optionNameExistsInItem(itemId, name, null)) {
            return fail(CustomizationErrorCode.OPTION_NAME_DUPLICATE);
        }

        if (request.getIngredientId() != null) {
            if (!ingredientExists(request.getIngredientId())) {
                return fail(CustomizationErrorCode.INGREDIENT_NOT_FOUND);
            }
        }

        CustomizationOptionEntity entity = CustomizationOptionEntity.builder()
                .itemId(itemId)
                .name(name)
                .price(request.getPrice())
                .ingredientId(request.getIngredientId())
                .ingredientQuantity(request.getIngredientQuantity())
                .status(CustomizationStatus.ACTIVE.getValue())
                .build();

        optionMapper.insert(entity);

        // New option is available under an item — invalidate options and product detail caches
        cacheEvictionService.evictCustomizationOptionsBizByItem(itemId);
        cacheEvictionService.evictProductBizDetailAllClear();

        return ApiResponse.success(customizationConverter.toCreateOptionResponse(entity));
    }

    @Override
    public ApiResponse<List<CustomizationOptionDetailResponse>> listOptions(Long itemId) {
        CustomizationItemEntity item = getActiveItemById(itemId);
        if (item == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        List<CustomizationOptionEntity> options = optionMapper.selectList(
                new LambdaQueryWrapper<CustomizationOptionEntity>()
                        .eq(CustomizationOptionEntity::getItemId, itemId)
                        .ne(CustomizationOptionEntity::getStatus, CustomizationStatus.DISABLED.getValue())
                        .orderByAsc(CustomizationOptionEntity::getId));

        return ApiResponse.success(options.stream()
                .map(customizationConverter::toOptionDetailResponse)
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CustomizationOptionDetailResponse> updateOption(Long id,
            UpdateCustomizationOptionRequest request) {
        CustomizationOptionEntity entity = getActiveOptionById(id);
        if (entity == null) {
            return fail(CustomizationErrorCode.OPTION_NOT_FOUND);
        }

        String name = request.getName().trim();
        if (optionNameExistsInItem(entity.getItemId(), name, id)) {
            return fail(CustomizationErrorCode.OPTION_NAME_DUPLICATE);
        }

        if (request.getIngredientId() != null) {
            if (!ingredientExists(request.getIngredientId())) {
                return fail(CustomizationErrorCode.INGREDIENT_NOT_FOUND);
            }
        }

        entity.setName(name);
        entity.setPrice(request.getPrice());
        entity.setIngredientId(request.getIngredientId());
        entity.setIngredientQuantity(request.getIngredientQuantity());
        entity.setStatus(request.getStatus());
        optionMapper.updateById(entity);

        cacheEvictionService.evictCustomizationOptionsBizByItem(entity.getItemId());
        cacheEvictionService.evictProductBizDetailAllClear();

        return ApiResponse.success(customizationConverter.toOptionDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> deleteOption(Long id) {
        CustomizationOptionEntity entity = getActiveOptionById(id);
        if (entity == null) {
            return fail(CustomizationErrorCode.OPTION_NOT_FOUND);
        }

        entity.setStatus(CustomizationStatus.DISABLED.getValue());
        optionMapper.updateById(entity);

        cacheEvictionService.evictCustomizationOptionsBizByItem(entity.getItemId());
        cacheEvictionService.evictProductBizDetailAllClear();

        return ApiResponse.success();
    }

    // ---- Helpers ----

    private CustomizationItemEntity getActiveItemById(Long itemId) {
        return itemMapper.selectOne(new LambdaQueryWrapper<CustomizationItemEntity>()
                .eq(CustomizationItemEntity::getId, itemId)
                .ne(CustomizationItemEntity::getStatus, CustomizationStatus.DISABLED.getValue()));
    }

    private CustomizationOptionEntity getActiveOptionById(Long optionId) {
        return optionMapper.selectOne(new LambdaQueryWrapper<CustomizationOptionEntity>()
                .eq(CustomizationOptionEntity::getId, optionId)
                .ne(CustomizationOptionEntity::getStatus, CustomizationStatus.DISABLED.getValue()));
    }

    private boolean optionNameExistsInItem(Long itemId, String name, Long excludeId) {
        return optionMapper.exists(new LambdaQueryWrapper<CustomizationOptionEntity>()
                .eq(CustomizationOptionEntity::getItemId, itemId)
                .eq(CustomizationOptionEntity::getName, name)
                .ne(CustomizationOptionEntity::getStatus, CustomizationStatus.DISABLED.getValue())
                .ne(excludeId != null, CustomizationOptionEntity::getId, excludeId));
    }

    private boolean ingredientExists(Long ingredientId) {
        return ingredientMapper.exists(new LambdaQueryWrapper<IngredientEntity>()
                .eq(IngredientEntity::getId, ingredientId)
                .eq(IngredientEntity::getStatus, IngredientStatus.ACTIVE.getValue()));
    }

    private <T> ApiResponse<T> fail(CustomizationErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}

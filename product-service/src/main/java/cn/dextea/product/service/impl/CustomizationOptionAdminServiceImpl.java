package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.dto.request.BindOptionIngredientRequest;
import cn.dextea.product.dto.request.CreateCustomizationOptionRequest;
import cn.dextea.product.dto.request.UpdateCustomizationOptionRequest;
import cn.dextea.product.dto.response.CreateCustomizationOptionResponse;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;
import cn.dextea.product.dto.response.OptionIngredientResponse;
import cn.dextea.product.entity.CustomizationOptionIngredientEntity;
import cn.dextea.product.entity.IngredientEntity;
import cn.dextea.product.entity.ProductCustomizationItemEntity;
import cn.dextea.product.entity.ProductCustomizationOptionEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.IngredientStatus;
import cn.dextea.product.mapper.CustomizationOptionIngredientMapper;
import cn.dextea.product.mapper.IngredientMapper;
import cn.dextea.product.mapper.ProductCustomizationItemMapper;
import cn.dextea.product.mapper.ProductCustomizationOptionMapper;
import cn.dextea.product.service.CustomizationOptionAdminService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CustomizationOptionAdminServiceImpl implements CustomizationOptionAdminService {

    private static final int STATUS_ACTIVE = 1;
    private static final int STATUS_DELETED = 0;

    private final ProductCustomizationItemMapper itemMapper;
    private final ProductCustomizationOptionMapper optionMapper;
    private final CustomizationOptionIngredientMapper bindingMapper;
    private final IngredientMapper ingredientMapper;
    private final CustomizationConverter customizationConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CreateCustomizationOptionResponse> createOption(Long itemId,
            CreateCustomizationOptionRequest request) {
        ProductCustomizationItemEntity item = getActiveItemById(itemId);
        if (item == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        String name = request.getName().trim();
        if (optionNameExistsInItem(itemId, name, null)) {
            return fail(CustomizationErrorCode.OPTION_NAME_DUPLICATE);
        }

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefaultInItem(itemId);
        }

        BigDecimal priceAdjustment = request.getPriceAdjustment() != null
                ? request.getPriceAdjustment() : BigDecimal.ZERO;

        ProductCustomizationOptionEntity entity = ProductCustomizationOptionEntity.builder()
                .itemId(itemId)
                .name(name)
                .priceAdjustment(priceAdjustment)
                .sortOrder(request.getSortOrder())
                .isDefault(request.getIsDefault())
                .status(STATUS_ACTIVE)
                .build();

        optionMapper.insert(entity);
        return ApiResponse.success(customizationConverter.toCreateCustomizationOptionResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CustomizationOptionDetailResponse> updateOption(Long optionId,
            UpdateCustomizationOptionRequest request) {
        ProductCustomizationOptionEntity entity = getActiveOptionById(optionId);
        if (entity == null) {
            return fail(CustomizationErrorCode.OPTION_NOT_FOUND);
        }

        String name = request.getName().trim();
        if (optionNameExistsInItem(entity.getItemId(), name, optionId)) {
            return fail(CustomizationErrorCode.OPTION_NAME_DUPLICATE);
        }

        if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(entity.getIsDefault())) {
            clearDefaultInItem(entity.getItemId());
        }

        BigDecimal priceAdjustment = request.getPriceAdjustment() != null
                ? request.getPriceAdjustment() : BigDecimal.ZERO;

        entity.setName(name);
        entity.setPriceAdjustment(priceAdjustment);
        entity.setSortOrder(request.getSortOrder());
        entity.setIsDefault(request.getIsDefault());
        optionMapper.updateById(entity);

        OptionIngredientResponse ingredientResponse = fetchIngredientResponse(optionId);
        return ApiResponse.success(
                customizationConverter.toCustomizationOptionDetailResponse(entity, ingredientResponse));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> deleteOption(Long optionId) {
        ProductCustomizationOptionEntity entity = getActiveOptionById(optionId);
        if (entity == null) {
            return fail(CustomizationErrorCode.OPTION_NOT_FOUND);
        }

        bindingMapper.delete(new LambdaQueryWrapper<CustomizationOptionIngredientEntity>()
                .eq(CustomizationOptionIngredientEntity::getOptionId, optionId));

        entity.setStatus(STATUS_DELETED);
        optionMapper.updateById(entity);
        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<OptionIngredientResponse> bindIngredient(Long optionId,
            BindOptionIngredientRequest request) {
        if (getActiveOptionById(optionId) == null) {
            return fail(CustomizationErrorCode.OPTION_NOT_FOUND);
        }

        IngredientEntity ingredient = getActiveIngredientById(request.getIngredientId());
        if (ingredient == null) {
            return fail(CustomizationErrorCode.INGREDIENT_NOT_FOUND);
        }

        CustomizationOptionIngredientEntity existing = bindingMapper.selectOne(
                new LambdaQueryWrapper<CustomizationOptionIngredientEntity>()
                        .eq(CustomizationOptionIngredientEntity::getOptionId, optionId)
                        .eq(CustomizationOptionIngredientEntity::getIngredientId, request.getIngredientId()));

        if (existing != null) {
            existing.setQuantity(request.getQuantity());
            bindingMapper.updateById(existing);
            return ApiResponse.success(customizationConverter.toOptionIngredientResponse(existing, ingredient));
        }

        CustomizationOptionIngredientEntity binding = CustomizationOptionIngredientEntity.builder()
                .optionId(optionId)
                .ingredientId(request.getIngredientId())
                .quantity(request.getQuantity())
                .build();

        bindingMapper.insert(binding);
        return ApiResponse.success(customizationConverter.toOptionIngredientResponse(binding, ingredient));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> unbindIngredient(Long optionId) {
        if (getActiveOptionById(optionId) == null) {
            return fail(CustomizationErrorCode.OPTION_NOT_FOUND);
        }

        int deleted = bindingMapper.delete(new LambdaQueryWrapper<CustomizationOptionIngredientEntity>()
                .eq(CustomizationOptionIngredientEntity::getOptionId, optionId));

        if (deleted == 0) {
            return fail(CustomizationErrorCode.INGREDIENT_BINDING_NOT_FOUND);
        }

        return ApiResponse.success();
    }

    // ---- Helpers ----

    private ProductCustomizationItemEntity getActiveItemById(Long itemId) {
        return itemMapper.selectOne(new LambdaQueryWrapper<ProductCustomizationItemEntity>()
                .eq(ProductCustomizationItemEntity::getId, itemId)
                .eq(ProductCustomizationItemEntity::getStatus, STATUS_ACTIVE));
    }

    private ProductCustomizationOptionEntity getActiveOptionById(Long optionId) {
        return optionMapper.selectOne(new LambdaQueryWrapper<ProductCustomizationOptionEntity>()
                .eq(ProductCustomizationOptionEntity::getId, optionId)
                .eq(ProductCustomizationOptionEntity::getStatus, STATUS_ACTIVE));
    }

    private boolean optionNameExistsInItem(Long itemId, String name, Long excludeOptionId) {
        return optionMapper.exists(new LambdaQueryWrapper<ProductCustomizationOptionEntity>()
                .eq(ProductCustomizationOptionEntity::getItemId, itemId)
                .eq(ProductCustomizationOptionEntity::getName, name)
                .eq(ProductCustomizationOptionEntity::getStatus, STATUS_ACTIVE)
                .ne(excludeOptionId != null, ProductCustomizationOptionEntity::getId, excludeOptionId));
    }

    private void clearDefaultInItem(Long itemId) {
        optionMapper.update(new LambdaUpdateWrapper<ProductCustomizationOptionEntity>()
                .eq(ProductCustomizationOptionEntity::getItemId, itemId)
                .eq(ProductCustomizationOptionEntity::getIsDefault, true)
                .eq(ProductCustomizationOptionEntity::getStatus, STATUS_ACTIVE)
                .set(ProductCustomizationOptionEntity::getIsDefault, false));
    }

    private IngredientEntity getActiveIngredientById(Long ingredientId) {
        return ingredientMapper.selectOne(new LambdaQueryWrapper<IngredientEntity>()
                .eq(IngredientEntity::getId, ingredientId)
                .eq(IngredientEntity::getStatus, IngredientStatus.ACTIVE.getValue()));
    }

    private OptionIngredientResponse fetchIngredientResponse(Long optionId) {
        CustomizationOptionIngredientEntity binding = bindingMapper.selectOne(
                new LambdaQueryWrapper<CustomizationOptionIngredientEntity>()
                        .eq(CustomizationOptionIngredientEntity::getOptionId, optionId));
        if (binding == null) {
            return null;
        }
        IngredientEntity ingredient = getActiveIngredientById(binding.getIngredientId());
        if (ingredient == null) {
            return null;
        }
        return customizationConverter.toOptionIngredientResponse(binding, ingredient);
    }

    private <T> ApiResponse<T> fail(CustomizationErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}

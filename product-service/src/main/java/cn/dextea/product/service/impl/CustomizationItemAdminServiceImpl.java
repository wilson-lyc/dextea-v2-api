package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.dto.request.CreateCustomizationItemRequest;
import cn.dextea.product.dto.request.UpdateCustomizationItemRequest;
import cn.dextea.product.dto.response.CreateCustomizationItemResponse;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;
import cn.dextea.product.dto.response.OptionIngredientResponse;
import cn.dextea.product.entity.CustomizationOptionIngredientEntity;
import cn.dextea.product.entity.IngredientEntity;
import cn.dextea.product.entity.ProductCustomizationItemEntity;
import cn.dextea.product.entity.ProductCustomizationOptionEntity;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.IngredientStatus;
import cn.dextea.product.enums.ProductStatus;
import cn.dextea.product.mapper.CustomizationOptionIngredientMapper;
import cn.dextea.product.mapper.IngredientMapper;
import cn.dextea.product.mapper.ProductCustomizationItemMapper;
import cn.dextea.product.mapper.ProductCustomizationOptionMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.service.CustomizationItemAdminService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomizationItemAdminServiceImpl implements CustomizationItemAdminService {

    private static final int STATUS_ACTIVE = 1;
    private static final int STATUS_DELETED = 0;

    private final ProductMapper productMapper;
    private final ProductCustomizationItemMapper itemMapper;
    private final ProductCustomizationOptionMapper optionMapper;
    private final CustomizationOptionIngredientMapper bindingMapper;
    private final IngredientMapper ingredientMapper;
    private final CustomizationConverter customizationConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CreateCustomizationItemResponse> createItem(Long productId,
            CreateCustomizationItemRequest request) {
        if (!productExists(productId)) {
            return fail(CustomizationErrorCode.PRODUCT_NOT_FOUND);
        }

        String name = request.getName().trim();
        if (itemNameExistsInProduct(productId, name, null)) {
            return fail(CustomizationErrorCode.ITEM_NAME_DUPLICATE);
        }

        ProductCustomizationItemEntity entity = ProductCustomizationItemEntity.builder()
                .productId(productId)
                .name(name)
                .sortOrder(request.getSortOrder())
                .isRequired(request.getIsRequired())
                .status(STATUS_ACTIVE)
                .build();

        itemMapper.insert(entity);
        return ApiResponse.success(customizationConverter.toCreateCustomizationItemResponse(entity));
    }

    @Override
    public ApiResponse<List<CustomizationItemDetailResponse>> getProductCustomizations(Long productId) {
        if (!productExists(productId)) {
            return fail(CustomizationErrorCode.PRODUCT_NOT_FOUND);
        }

        List<ProductCustomizationItemEntity> items = itemMapper.selectList(
                new LambdaQueryWrapper<ProductCustomizationItemEntity>()
                        .eq(ProductCustomizationItemEntity::getProductId, productId)
                        .eq(ProductCustomizationItemEntity::getStatus, STATUS_ACTIVE)
                        .orderByAsc(ProductCustomizationItemEntity::getSortOrder));

        if (items.isEmpty()) {
            return ApiResponse.success(Collections.emptyList());
        }

        List<Long> itemIds = items.stream().map(ProductCustomizationItemEntity::getId).collect(Collectors.toList());

        List<ProductCustomizationOptionEntity> options = optionMapper.selectList(
                new LambdaQueryWrapper<ProductCustomizationOptionEntity>()
                        .in(ProductCustomizationOptionEntity::getItemId, itemIds)
                        .eq(ProductCustomizationOptionEntity::getStatus, STATUS_ACTIVE)
                        .orderByAsc(ProductCustomizationOptionEntity::getSortOrder));

        Map<Long, OptionIngredientResponse> ingredientByOptionId = fetchIngredientResponses(options);

        Map<Long, List<CustomizationOptionDetailResponse>> optionsByItemId = options.stream()
                .collect(Collectors.groupingBy(
                        ProductCustomizationOptionEntity::getItemId,
                        Collectors.mapping(
                                o -> customizationConverter.toCustomizationOptionDetailResponse(
                                        o, ingredientByOptionId.get(o.getId())),
                                Collectors.toList())));

        List<CustomizationItemDetailResponse> result = items.stream()
                .map(item -> customizationConverter.toCustomizationItemDetailResponse(
                        item, optionsByItemId.getOrDefault(item.getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        return ApiResponse.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CustomizationItemDetailResponse> updateItem(Long itemId,
            UpdateCustomizationItemRequest request) {
        ProductCustomizationItemEntity entity = getActiveItemById(itemId);
        if (entity == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        String name = request.getName().trim();
        if (itemNameExistsInProduct(entity.getProductId(), name, itemId)) {
            return fail(CustomizationErrorCode.ITEM_NAME_DUPLICATE);
        }

        entity.setName(name);
        entity.setSortOrder(request.getSortOrder());
        entity.setIsRequired(request.getIsRequired());
        itemMapper.updateById(entity);

        List<ProductCustomizationOptionEntity> options = getActiveOptionsByItemId(itemId);
        Map<Long, OptionIngredientResponse> ingredientByOptionId = fetchIngredientResponses(options);

        List<CustomizationOptionDetailResponse> optionResponses = options.stream()
                .map(o -> customizationConverter.toCustomizationOptionDetailResponse(
                        o, ingredientByOptionId.get(o.getId())))
                .collect(Collectors.toList());

        return ApiResponse.success(
                customizationConverter.toCustomizationItemDetailResponse(entity, optionResponses));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> deleteItem(Long itemId) {
        ProductCustomizationItemEntity entity = getActiveItemById(itemId);
        if (entity == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        List<ProductCustomizationOptionEntity> options = getActiveOptionsByItemId(itemId);

        if (!options.isEmpty()) {
            List<Long> optionIds = options.stream()
                    .map(ProductCustomizationOptionEntity::getId)
                    .collect(Collectors.toList());

            // Physically remove ingredient bindings for all options under this item
            bindingMapper.delete(new LambdaQueryWrapper<CustomizationOptionIngredientEntity>()
                    .in(CustomizationOptionIngredientEntity::getOptionId, optionIds));

            // Soft-delete all options
            optionMapper.update(new LambdaUpdateWrapper<ProductCustomizationOptionEntity>()
                    .in(ProductCustomizationOptionEntity::getId, optionIds)
                    .set(ProductCustomizationOptionEntity::getStatus, STATUS_DELETED));
        }

        entity.setStatus(STATUS_DELETED);
        itemMapper.updateById(entity);
        return ApiResponse.success();
    }

    // ---- Helpers ----

    private boolean productExists(Long productId) {
        return productMapper.exists(new LambdaQueryWrapper<ProductEntity>()
                .eq(ProductEntity::getId, productId)
                .eq(ProductEntity::getStatus, ProductStatus.ENABLED.getValue()));
    }

    private ProductCustomizationItemEntity getActiveItemById(Long itemId) {
        return itemMapper.selectOne(new LambdaQueryWrapper<ProductCustomizationItemEntity>()
                .eq(ProductCustomizationItemEntity::getId, itemId)
                .eq(ProductCustomizationItemEntity::getStatus, STATUS_ACTIVE));
    }

    private boolean itemNameExistsInProduct(Long productId, String name, Long excludeItemId) {
        return itemMapper.exists(new LambdaQueryWrapper<ProductCustomizationItemEntity>()
                .eq(ProductCustomizationItemEntity::getProductId, productId)
                .eq(ProductCustomizationItemEntity::getName, name)
                .eq(ProductCustomizationItemEntity::getStatus, STATUS_ACTIVE)
                .ne(excludeItemId != null, ProductCustomizationItemEntity::getId, excludeItemId));
    }

    private List<ProductCustomizationOptionEntity> getActiveOptionsByItemId(Long itemId) {
        return optionMapper.selectList(new LambdaQueryWrapper<ProductCustomizationOptionEntity>()
                .eq(ProductCustomizationOptionEntity::getItemId, itemId)
                .eq(ProductCustomizationOptionEntity::getStatus, STATUS_ACTIVE)
                .orderByAsc(ProductCustomizationOptionEntity::getSortOrder));
    }

    private Map<Long, OptionIngredientResponse> fetchIngredientResponses(
            List<ProductCustomizationOptionEntity> options) {
        if (options.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> optionIds = options.stream()
                .map(ProductCustomizationOptionEntity::getId)
                .collect(Collectors.toList());

        List<CustomizationOptionIngredientEntity> bindings = bindingMapper.selectList(
                new LambdaQueryWrapper<CustomizationOptionIngredientEntity>()
                        .in(CustomizationOptionIngredientEntity::getOptionId, optionIds));

        if (bindings.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> ingredientIds = bindings.stream()
                .map(CustomizationOptionIngredientEntity::getIngredientId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, IngredientEntity> ingredientMap = ingredientMapper.selectList(
                new LambdaQueryWrapper<IngredientEntity>()
                        .in(IngredientEntity::getId, ingredientIds)
                        .eq(IngredientEntity::getStatus, IngredientStatus.ACTIVE.getValue()))
                .stream()
                .collect(Collectors.toMap(IngredientEntity::getId, Function.identity()));

        Map<Long, OptionIngredientResponse> result = new java.util.HashMap<>();
        for (CustomizationOptionIngredientEntity binding : bindings) {
            IngredientEntity ingredient = ingredientMap.get(binding.getIngredientId());
            if (ingredient != null) {
                result.put(binding.getOptionId(),
                        customizationConverter.toOptionIngredientResponse(binding, ingredient));
            }
        }
        return result;
    }

    private <T> ApiResponse<T> fail(CustomizationErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}

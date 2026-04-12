package cn.dextea.product.service.impl;

import cn.dextea.common.util.StringValueUtils;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.InventoryConverter;
import cn.dextea.product.dto.request.AdjustInventoryRequest;
import cn.dextea.product.dto.request.InventoryPageQueryRequest;
import cn.dextea.product.dto.request.SetInventoryRequest;
import cn.dextea.product.dto.response.InventoryDetailResponse;
import cn.dextea.product.entity.IngredientEntity;
import cn.dextea.product.entity.StoreIngredientInventoryEntity;
import cn.dextea.product.enums.IngredientStatus;
import cn.dextea.product.enums.InventoryErrorCode;
import cn.dextea.product.mapper.IngredientMapper;
import cn.dextea.product.mapper.StoreIngredientInventoryMapper;
import cn.dextea.product.service.InventoryAdminService;
import cn.dextea.store.api.feign.StoreInternalFeign;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryAdminServiceImpl implements InventoryAdminService {

    private final StoreIngredientInventoryMapper inventoryMapper;
    private final IngredientMapper ingredientMapper;
    private final InventoryConverter inventoryConverter;
    private final StoreInternalFeign storeInternalFeign;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<InventoryDetailResponse> setInventory(
            Long storeId, Long ingredientId, SetInventoryRequest request) {

        if (!isStoreValid(storeId)) {
            return fail(InventoryErrorCode.STORE_NOT_FOUND);
        }

        IngredientEntity ingredient = getActiveIngredient(ingredientId);
        if (ingredient == null) {
            return fail(InventoryErrorCode.INGREDIENT_NOT_FOUND);
        }

        String unit = request.getUnit().trim();
        BigDecimal quantity = request.getQuantity();
        BigDecimal warnThreshold = request.getWarnThreshold();
        LocalDateTime now = LocalDateTime.now();

        StoreIngredientInventoryEntity existing = findByStoreAndIngredient(storeId, ingredientId);

        if (existing != null) {
            existing.setQuantity(quantity);
            existing.setUnit(unit);
            existing.setWarnThreshold(warnThreshold);
            existing.setUpdateTime(now);
            // treat any explicit set as a restock event
            existing.setLastRestockTime(now);

            if (inventoryMapper.update(existing, new LambdaQueryWrapper<StoreIngredientInventoryEntity>()
                    .eq(StoreIngredientInventoryEntity::getStoreId, existing.getStoreId())
                    .eq(StoreIngredientInventoryEntity::getIngredientId, existing.getIngredientId())) != 1) {
                return fail(InventoryErrorCode.SET_FAILED);
            }
            return ApiResponse.success(inventoryConverter.toInventoryDetailResponse(existing, ingredient));
        }

        StoreIngredientInventoryEntity entity = StoreIngredientInventoryEntity.builder()
                .storeId(storeId)
                .ingredientId(ingredientId)
                .quantity(quantity)
                .unit(unit)
                .warnThreshold(warnThreshold)
                .lastRestockTime(now)
                .createTime(now)
                .updateTime(now)
                .build();

        if (inventoryMapper.insert(entity) != 1) {
            return fail(InventoryErrorCode.SET_FAILED);
        }
        return ApiResponse.success(inventoryConverter.toInventoryDetailResponse(entity, ingredient));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<InventoryDetailResponse> adjustInventory(
            Long storeId, Long ingredientId, AdjustInventoryRequest request) {

        StoreIngredientInventoryEntity existing = findByStoreAndIngredient(storeId, ingredientId);
        if (existing == null) {
            return fail(InventoryErrorCode.INVENTORY_NOT_FOUND);
        }

        BigDecimal newQuantity = existing.getQuantity().add(request.getDelta());
        if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
            return fail(InventoryErrorCode.INSUFFICIENT_STOCK);
        }

        LocalDateTime now = LocalDateTime.now();
        existing.setQuantity(newQuantity);
        existing.setUpdateTime(now);

        // record restock timestamp only when adding stock
        if (request.getDelta().compareTo(BigDecimal.ZERO) > 0) {
            existing.setLastRestockTime(now);
        }

        if (inventoryMapper.update(existing, new LambdaQueryWrapper<StoreIngredientInventoryEntity>()
                .eq(StoreIngredientInventoryEntity::getStoreId, existing.getStoreId())
                .eq(StoreIngredientInventoryEntity::getIngredientId, existing.getIngredientId())) != 1) {
            return fail(InventoryErrorCode.ADJUST_FAILED);
        }

        IngredientEntity ingredient = getActiveIngredient(ingredientId);
        return ApiResponse.success(inventoryConverter.toInventoryDetailResponse(existing, ingredient));
    }

    @Override
    public ApiResponse<IPage<InventoryDetailResponse>> getInventoryPage(
            Long storeId, InventoryPageQueryRequest request) {

        // Resolve ingredient ID list when a name filter is specified
        List<Long> filteredIngredientIds = null;
        if (StringValueUtils.hasText(request.getIngredientName())) {
            filteredIngredientIds = ingredientMapper.selectList(
                    new LambdaQueryWrapper<IngredientEntity>()
                            .eq(IngredientEntity::getStatus, IngredientStatus.ACTIVE.getValue())
                            .like(IngredientEntity::getName, request.getIngredientName().trim())
                            .select(IngredientEntity::getId)
            ).stream().map(IngredientEntity::getId).collect(Collectors.toList());

            // No ingredients matched the name filter — return empty page immediately
            if (filteredIngredientIds.isEmpty()) {
                return ApiResponse.success(new Page<>(request.getCurrent(), request.getSize()));
            }
        }

        LambdaQueryWrapper<StoreIngredientInventoryEntity> wrapper =
                new LambdaQueryWrapper<StoreIngredientInventoryEntity>()
                        .eq(StoreIngredientInventoryEntity::getStoreId, storeId)
                        .in(filteredIngredientIds != null,
                                StoreIngredientInventoryEntity::getIngredientId, filteredIngredientIds)
                        .apply(Boolean.TRUE.equals(request.getLowStock()),
                                "quantity <= warn_threshold")
                        .orderByDesc(StoreIngredientInventoryEntity::getCreateTime);

        IPage<StoreIngredientInventoryEntity> entityPage = inventoryMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), wrapper);

        // Batch-load ingredient details for the current page
        Map<Long, IngredientEntity> ingredientMap = buildIngredientMap(entityPage.getRecords());

        IPage<InventoryDetailResponse> responsePage = entityPage.convert(inv ->
                inventoryConverter.toInventoryDetailResponse(inv, ingredientMap.get(inv.getIngredientId())));
        return ApiResponse.success(responsePage);
    }

    @Override
    public ApiResponse<InventoryDetailResponse> getInventoryDetail(Long storeId, Long ingredientId) {
        StoreIngredientInventoryEntity entity = findByStoreAndIngredient(storeId, ingredientId);
        if (entity == null) {
            return fail(InventoryErrorCode.INVENTORY_NOT_FOUND);
        }

        IngredientEntity ingredient = getActiveIngredient(ingredientId);
        if (ingredient == null) {
            return fail(InventoryErrorCode.INGREDIENT_NOT_FOUND);
        }
        return ApiResponse.success(inventoryConverter.toInventoryDetailResponse(entity, ingredient));
    }

    // ---- Helpers ----

    private StoreIngredientInventoryEntity findByStoreAndIngredient(Long storeId, Long ingredientId) {
        return inventoryMapper.selectOne(
                new LambdaQueryWrapper<StoreIngredientInventoryEntity>()
                        .eq(StoreIngredientInventoryEntity::getStoreId, storeId)
                        .eq(StoreIngredientInventoryEntity::getIngredientId, ingredientId));
    }

    private IngredientEntity getActiveIngredient(Long ingredientId) {
        return ingredientMapper.selectOne(
                new LambdaQueryWrapper<IngredientEntity>()
                        .eq(IngredientEntity::getId, ingredientId)
                        .eq(IngredientEntity::getStatus, IngredientStatus.ACTIVE.getValue()));
    }

    private boolean isStoreValid(Long storeId) {
        try {
            ApiResponse<cn.dextea.store.api.dto.response.StoreValidityResponse> response =
                    storeInternalFeign.checkStoreValidity(storeId);
            return response != null
                    && response.getData() != null
                    && Boolean.TRUE.equals(response.getData().getValid());
        } catch (Exception e) {
            return false;
        }
    }

    private Map<Long, IngredientEntity> buildIngredientMap(
            List<StoreIngredientInventoryEntity> inventories) {
        if (inventories.isEmpty()) {
            return Map.of();
        }
        Set<Long> ids = inventories.stream()
                .map(StoreIngredientInventoryEntity::getIngredientId)
                .collect(Collectors.toSet());
        return ingredientMapper.selectList(
                new LambdaQueryWrapper<IngredientEntity>().in(IngredientEntity::getId, ids)
        ).stream().collect(Collectors.toMap(IngredientEntity::getId, e -> e));
    }

    private <T> ApiResponse<T> fail(InventoryErrorCode code) {
        return ApiResponse.fail(code.getCode(), code.getMsg());
    }
}

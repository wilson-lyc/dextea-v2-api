package cn.dextea.product.service.impl;

import cn.dextea.common.util.StringValueUtils;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.IngredientPageQueryRequest;
import cn.dextea.product.dto.request.UpdateStoreIngredientInventoryRequest;
import cn.dextea.product.dto.response.StoreIngredientInventoryResponse;
import cn.dextea.product.entity.IngredientEntity;
import cn.dextea.product.entity.StoreIngredientInventoryEntity;
import cn.dextea.product.enums.IngredientErrorCode;
import cn.dextea.product.enums.IngredientStatus;
import cn.dextea.product.mapper.IngredientMapper;
import cn.dextea.product.mapper.StoreIngredientInventoryMapper;
import cn.dextea.product.service.IngredientBizService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientBizServiceImpl implements IngredientBizService {

    private final IngredientMapper ingredientMapper;
    private final StoreIngredientInventoryMapper inventoryMapper;

    @Override
    public ApiResponse<IPage<StoreIngredientInventoryResponse>> getInventoryPage(Long storeId, IngredientPageQueryRequest request) {
        LambdaQueryWrapper<IngredientEntity> query = new LambdaQueryWrapper<IngredientEntity>()
                .eq(IngredientEntity::getStatus, IngredientStatus.ACTIVE.getValue())
                .like(StringValueUtils.hasText(request.getName()), IngredientEntity::getName, StringValueUtils.trim(request.getName()))
                .orderByDesc(IngredientEntity::getId);
        IPage<IngredientEntity> ingredientPage = ingredientMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), query);

        List<IngredientEntity> ingredients = ingredientPage.getRecords();
        if (ingredients.isEmpty()) {
            return ApiResponse.success(ingredientPage.convert(i -> toInventoryResponse(i, null)));
        }

        List<Long> ingredientIds = ingredients.stream().map(IngredientEntity::getId).toList();
        LambdaQueryWrapper<StoreIngredientInventoryEntity> invQuery = new LambdaQueryWrapper<StoreIngredientInventoryEntity>()
                .eq(StoreIngredientInventoryEntity::getStoreId, storeId)
                .in(StoreIngredientInventoryEntity::getIngredientId, ingredientIds);
        Map<Long, StoreIngredientInventoryEntity> inventoryMap = inventoryMapper.selectList(invQuery).stream()
                .collect(Collectors.toMap(StoreIngredientInventoryEntity::getIngredientId, v -> v));

        return ApiResponse.success(ingredientPage.convert(i -> toInventoryResponse(i, inventoryMap.get(i.getId()))));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> updateInventory(Long ingredientId, UpdateStoreIngredientInventoryRequest request) {
        LambdaQueryWrapper<IngredientEntity> query = new LambdaQueryWrapper<IngredientEntity>()
                .eq(IngredientEntity::getId, ingredientId)
                .eq(IngredientEntity::getStatus, IngredientStatus.ACTIVE.getValue());
        IngredientEntity ingredient = ingredientMapper.selectOne(query);
        if (ingredient == null) {
            return fail(IngredientErrorCode.INGREDIENT_NOT_FOUND);
        }

        int rows = inventoryMapper.upsertInventory(
                request.getStoreId(),
                ingredientId,
                request.getQuantity(),
                ingredient.getUnit(),
                request.getWarnThreshold());
        if (rows == 0) {
            return fail(IngredientErrorCode.INVENTORY_UPDATE_FAILED);
        }

        return ApiResponse.success();
    }

    private StoreIngredientInventoryResponse toInventoryResponse(IngredientEntity ingredient,
                                                                  StoreIngredientInventoryEntity inventory) {
        return StoreIngredientInventoryResponse.builder()
                .ingredientId(ingredient.getId())
                .ingredientName(ingredient.getName())
                .unit(ingredient.getUnit())
                .storageDuration(ingredient.getStorageDuration())
                .storageDurationUnit(ingredient.getStorageDurationUnit())
                .storageMethod(ingredient.getStorageMethod())
                .preparedExpiry(ingredient.getPreparedExpiry())
                .preparedExpiryUnit(ingredient.getPreparedExpiryUnit())
                .quantity(inventory != null ? inventory.getQuantity() : BigDecimal.ZERO)
                .warnThreshold(inventory != null ? inventory.getWarnThreshold() : null)
                .lastRestockTime(inventory != null ? inventory.getLastRestockTime() : null)
                .build();
    }

    private <T> ApiResponse<T> fail(IngredientErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}

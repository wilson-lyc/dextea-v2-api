package cn.dextea.product.service.impl;

import cn.dextea.common.util.StringValueUtils;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.cache.CacheNames;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.dto.request.CustomizationItemPageQueryWithStoreIdRequest;
import cn.dextea.product.dto.request.UpdateStoreCustomizationItemSaleRequest;
import cn.dextea.product.dto.response.CustomizationItemWithStoreStatusResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.entity.StoreCustomizationItemRelEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.CustomizationStatus;
import cn.dextea.product.enums.StoreCustomizationSaleStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.StoreCustomizationItemRelMapper;
import cn.dextea.product.service.CustomizationItemBizService;
import cn.dextea.product.service.ProductCacheEvictionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomizationItemBizServiceImpl implements CustomizationItemBizService {

    private final CustomizationItemMapper itemMapper;
    private final StoreCustomizationItemRelMapper storeItemRelMapper;
    private final CustomizationConverter customizationConverter;
    private final ProductCacheEvictionService cacheEvictionService;

    @Override
    @Cacheable(
            cacheNames = CacheNames.CUSTOMIZATION_ITEM_BIZ,
            key = "'store:' + #request.storeId + ':p:' + #request.current + ':s:' + #request.size + ':n:' + (#request.name ?: '')",
            unless = "#result.code != 0"
    )
    public ApiResponse<IPage<CustomizationItemWithStoreStatusResponse>> page(
            CustomizationItemPageQueryWithStoreIdRequest request) {
        Long storeId = request.getStoreId();

        LambdaQueryWrapper<CustomizationItemEntity> query = new LambdaQueryWrapper<CustomizationItemEntity>()
                .eq(CustomizationItemEntity::getStatus, CustomizationStatus.ACTIVE.getValue())
                .like(StringValueUtils.hasText(request.getName()), CustomizationItemEntity::getName,
                        request.getName() == null ? "" : request.getName().trim())
                .orderByDesc(CustomizationItemEntity::getId);

        IPage<CustomizationItemEntity> itemPage = itemMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), query);

        List<CustomizationItemEntity> items = itemPage.getRecords();
        if (items.isEmpty()) {
            return ApiResponse.success(itemPage.convert(
                    i -> customizationConverter.toItemWithStoreStatusResponse(
                            i, StoreCustomizationSaleStatus.SOLD_OUT.getValue())));
        }

        List<Long> itemIds = items.stream().map(CustomizationItemEntity::getId).collect(Collectors.toList());
        Set<Long> onSaleItemIds = storeItemRelMapper.selectList(
                new LambdaQueryWrapper<StoreCustomizationItemRelEntity>()
                        .eq(StoreCustomizationItemRelEntity::getStoreId, storeId)
                        .in(StoreCustomizationItemRelEntity::getItemId, itemIds))
                .stream()
                .map(StoreCustomizationItemRelEntity::getItemId)
                .collect(Collectors.toSet());

        return ApiResponse.success(itemPage.convert(entity -> {
            int storeStatus = onSaleItemIds.contains(entity.getId())
                    ? StoreCustomizationSaleStatus.ON_SALE.getValue()
                    : StoreCustomizationSaleStatus.SOLD_OUT.getValue();
            return customizationConverter.toItemWithStoreStatusResponse(entity, storeStatus);
        }));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> updateSaleStatus(Long itemId, UpdateStoreCustomizationItemSaleRequest request) {
        CustomizationItemEntity item = itemMapper.selectOne(new LambdaQueryWrapper<CustomizationItemEntity>()
                .eq(CustomizationItemEntity::getId, itemId)
                .eq(CustomizationItemEntity::getStatus, CustomizationStatus.ACTIVE.getValue()));
        if (item == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        Long storeId = request.getStoreId();
        LambdaQueryWrapper<StoreCustomizationItemRelEntity> relQuery =
                new LambdaQueryWrapper<StoreCustomizationItemRelEntity>()
                        .eq(StoreCustomizationItemRelEntity::getStoreId, storeId)
                        .eq(StoreCustomizationItemRelEntity::getItemId, itemId);

        if (Boolean.TRUE.equals(request.getOnSale())) {
            if (!storeItemRelMapper.exists(relQuery)) {
                StoreCustomizationItemRelEntity rel = StoreCustomizationItemRelEntity.builder()
                        .storeId(storeId)
                        .itemId(itemId)
                        .build();
                if (storeItemRelMapper.insert(rel) != 1) {
                    return fail(CustomizationErrorCode.STORE_ITEM_SALE_STATUS_UPDATE_FAILED);
                }
            }
        } else {
            storeItemRelMapper.delete(relQuery);
        }

        cacheEvictionService.evictCustomizationItemBizAll();

        return ApiResponse.success();
    }

    private <T> ApiResponse<T> fail(CustomizationErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}

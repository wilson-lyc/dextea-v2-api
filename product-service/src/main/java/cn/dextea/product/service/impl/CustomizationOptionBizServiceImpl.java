package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.cache.CacheNames;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.dto.request.CustomizationOptionListWithStoreIdRequest;
import cn.dextea.product.dto.request.UpdateStoreCustomizationOptionSaleRequest;
import cn.dextea.product.dto.response.CustomizationOptionWithStoreStatusResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.entity.CustomizationOptionEntity;
import cn.dextea.product.entity.StoreCustomizationOptionRelEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.CustomizationStatus;
import cn.dextea.product.enums.StoreCustomizationSaleStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.CustomizationOptionMapper;
import cn.dextea.product.mapper.StoreCustomizationOptionRelMapper;
import cn.dextea.product.service.CustomizationOptionBizService;
import cn.dextea.product.service.ProductCacheEvictionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomizationOptionBizServiceImpl implements CustomizationOptionBizService {

    private final CustomizationItemMapper itemMapper;
    private final CustomizationOptionMapper optionMapper;
    private final StoreCustomizationOptionRelMapper storeOptionRelMapper;
    private final CustomizationConverter customizationConverter;
    private final ProductCacheEvictionService cacheEvictionService;

    @Override
    @Cacheable(
            cacheNames = CacheNames.CUSTOMIZATION_OPTIONS_BIZ,
            key = "'item:' + #itemId + ':store:' + #request.storeId",
            unless = "#result.code != 0"
    )
    public ApiResponse<List<CustomizationOptionWithStoreStatusResponse>> listOptions(Long itemId,
            CustomizationOptionListWithStoreIdRequest request) {
        CustomizationItemEntity item = itemMapper.selectOne(new LambdaQueryWrapper<CustomizationItemEntity>()
                .eq(CustomizationItemEntity::getId, itemId)
                .eq(CustomizationItemEntity::getStatus, CustomizationStatus.ACTIVE.getValue()));
        if (item == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        List<CustomizationOptionEntity> options = optionMapper.selectList(
                new LambdaQueryWrapper<CustomizationOptionEntity>()
                        .eq(CustomizationOptionEntity::getItemId, itemId)
                        .eq(CustomizationOptionEntity::getStatus, CustomizationStatus.ACTIVE.getValue())
                        .orderByAsc(CustomizationOptionEntity::getId));

        if (options.isEmpty()) {
            return ApiResponse.success(List.of());
        }

        Long storeId = request.getStoreId();
        List<Long> optionIds = options.stream().map(CustomizationOptionEntity::getId).collect(Collectors.toList());
        Set<Long> onSaleOptionIds = storeOptionRelMapper.selectList(
                new LambdaQueryWrapper<StoreCustomizationOptionRelEntity>()
                        .eq(StoreCustomizationOptionRelEntity::getStoreId, storeId)
                        .in(StoreCustomizationOptionRelEntity::getOptionId, optionIds))
                .stream()
                .map(StoreCustomizationOptionRelEntity::getOptionId)
                .collect(Collectors.toSet());

        List<CustomizationOptionWithStoreStatusResponse> result = options.stream()
                .map(entity -> {
                    int storeStatus = onSaleOptionIds.contains(entity.getId())
                            ? StoreCustomizationSaleStatus.ON_SALE.getValue()
                            : StoreCustomizationSaleStatus.SOLD_OUT.getValue();
                    return customizationConverter.toOptionWithStoreStatusResponse(entity, storeStatus);
                })
                .collect(Collectors.toList());

        return ApiResponse.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> updateSaleStatus(Long optionId, UpdateStoreCustomizationOptionSaleRequest request) {
        CustomizationOptionEntity option = optionMapper.selectOne(new LambdaQueryWrapper<CustomizationOptionEntity>()
                .eq(CustomizationOptionEntity::getId, optionId)
                .eq(CustomizationOptionEntity::getStatus, CustomizationStatus.ACTIVE.getValue()));
        if (option == null) {
            return fail(CustomizationErrorCode.OPTION_NOT_FOUND);
        }

        Long storeId = request.getStoreId();
        LambdaQueryWrapper<StoreCustomizationOptionRelEntity> relQuery =
                new LambdaQueryWrapper<StoreCustomizationOptionRelEntity>()
                        .eq(StoreCustomizationOptionRelEntity::getStoreId, storeId)
                        .eq(StoreCustomizationOptionRelEntity::getOptionId, optionId);

        if (Boolean.TRUE.equals(request.getOnSale())) {
            if (!storeOptionRelMapper.exists(relQuery)) {
                StoreCustomizationOptionRelEntity rel = StoreCustomizationOptionRelEntity.builder()
                        .storeId(storeId)
                        .optionId(optionId)
                        .build();
                if (storeOptionRelMapper.insert(rel) != 1) {
                    return fail(CustomizationErrorCode.STORE_OPTION_SALE_STATUS_UPDATE_FAILED);
                }
            }
        } else {
            storeOptionRelMapper.delete(relQuery);
        }

        cacheEvictionService.evictCustomizationOptionsBizByItem(option.getItemId());

        return ApiResponse.success();
    }

    private <T> ApiResponse<T> fail(CustomizationErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}

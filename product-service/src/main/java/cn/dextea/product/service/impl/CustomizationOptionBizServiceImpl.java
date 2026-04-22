package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.dto.request.QueryStoreItemOptionsRequest;
import cn.dextea.product.dto.request.UpdateStoreCustomizationOptionStatusRequest;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;
import cn.dextea.product.entity.CustomizationOptionEntity;
import cn.dextea.product.entity.StoreCustomizationOptionStatusEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.StoreCustomizationStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.CustomizationOptionMapper;
import cn.dextea.product.mapper.StoreCustomizationOptionStatusMapper;
import cn.dextea.product.service.CustomizationOptionBizService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomizationOptionBizServiceImpl implements CustomizationOptionBizService {

    private final CustomizationItemMapper itemMapper;
    private final CustomizationOptionMapper optionMapper;
    private final StoreCustomizationOptionStatusMapper storeOptionStatusMapper;
    private final CustomizationConverter customizationConverter;

    @Override
    public ApiResponse<List<CustomizationOptionDetailResponse>> getItemOptionsList(Long itemId,
                                                                                   QueryStoreItemOptionsRequest request) {
        if (itemMapper.selectById(itemId) == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        Long storeId = request.getStoreId();

        List<CustomizationOptionEntity> options = optionMapper.selectList(
                new LambdaQueryWrapper<CustomizationOptionEntity>()
                        .eq(CustomizationOptionEntity::getItemId, itemId)
                        .orderByAsc(CustomizationOptionEntity::getId));

        if (options.isEmpty()) {
            return ApiResponse.success(List.of());
        }

        List<Long> optionIds = options.stream().map(CustomizationOptionEntity::getId).toList();
        Map<Long, Integer> optionStatusMap = storeOptionStatusMapper.selectList(
                new LambdaQueryWrapper<StoreCustomizationOptionStatusEntity>()
                        .eq(StoreCustomizationOptionStatusEntity::getStoreId, storeId)
                        .in(StoreCustomizationOptionStatusEntity::getOptionId, optionIds))
                .stream()
                .collect(Collectors.toMap(
                        StoreCustomizationOptionStatusEntity::getOptionId,
                        StoreCustomizationOptionStatusEntity::getStatus,
                        (left, right) -> right));
        List<CustomizationOptionDetailResponse> result = options.stream()
                .map(entity -> {
                    int storeStatus = optionStatusMap.getOrDefault(entity.getId(), StoreCustomizationStatus.DISABLED.getValue());
                    return customizationConverter.toOptionDetailResponse(entity, storeStatus);
                })
                .collect(Collectors.toList());

        return ApiResponse.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> updateOptionStoreStatus(Long optionId, UpdateStoreCustomizationOptionStatusRequest request) {
        if (optionMapper.selectById(optionId) == null) {
            return fail(CustomizationErrorCode.OPTION_NOT_FOUND);
        }

        Long storeId = request.getStoreId();
        StoreCustomizationOptionStatusEntity existing = storeOptionStatusMapper.selectOne(
                new LambdaQueryWrapper<StoreCustomizationOptionStatusEntity>()
                        .eq(StoreCustomizationOptionStatusEntity::getStoreId, storeId)
                        .eq(StoreCustomizationOptionStatusEntity::getOptionId, optionId));
        if (existing == null) {
            StoreCustomizationOptionStatusEntity statusEntity = StoreCustomizationOptionStatusEntity.builder()
                    .storeId(storeId)
                    .optionId(optionId)
                    .status(request.getStatus())
                    .build();
            if (storeOptionStatusMapper.insert(statusEntity) != 1) {
                return fail(CustomizationErrorCode.STORE_OPTION_SALE_STATUS_UPDATE_FAILED);
            }
        } else {
            int rows = storeOptionStatusMapper.update(null,
                    new LambdaUpdateWrapper<StoreCustomizationOptionStatusEntity>()
                            .eq(StoreCustomizationOptionStatusEntity::getStoreId, storeId)
                            .eq(StoreCustomizationOptionStatusEntity::getOptionId, optionId)
                            .set(StoreCustomizationOptionStatusEntity::getStatus, request.getStatus()));
            if (rows != 1) {
                return fail(CustomizationErrorCode.STORE_OPTION_SALE_STATUS_UPDATE_FAILED);
            }
        }

        return ApiResponse.success();
    }

    private <T> ApiResponse<T> fail(CustomizationErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}

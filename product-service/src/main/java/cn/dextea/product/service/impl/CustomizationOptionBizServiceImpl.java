package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.dto.request.CustomizationOptionListWithStoreIdRequest;
import cn.dextea.product.dto.request.UpdateStoreCustomizationOptionStatusRequest;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;
import cn.dextea.product.entity.CustomizationOptionEntity;
import cn.dextea.product.entity.StoreCustomizationOptionStatusEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.CustomizationStatus;
import cn.dextea.product.enums.StoreCustomizationStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.CustomizationOptionMapper;
import cn.dextea.product.mapper.StoreCustomizationOptionRelMapper;
import cn.dextea.product.service.CustomizationOptionBizService;
import cn.dextea.product.service.support.CustomizationOptionStoreStatusSyncSupport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomizationOptionBizServiceImpl implements CustomizationOptionBizService {

    private final CustomizationItemMapper itemMapper;
    private final CustomizationOptionMapper optionMapper;
    private final StoreCustomizationOptionRelMapper storeOptionRelMapper;
    private final CustomizationConverter customizationConverter;
    private final CustomizationOptionStoreStatusSyncSupport customizationOptionStoreStatusSyncSupport;

    @Override
    public ApiResponse<List<CustomizationOptionDetailResponse>> listOptions(Long itemId,
            CustomizationOptionListWithStoreIdRequest request) {
        if (itemMapper.selectById(itemId) == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        Long storeId = request.getStoreId();
        Integer storeStatus = request.getStoreStatus();

        LambdaQueryWrapper<CustomizationOptionEntity> optionQuery = new LambdaQueryWrapper<CustomizationOptionEntity>()
                .eq(CustomizationOptionEntity::getItemId, itemId)
                .eq(CustomizationOptionEntity::getStatus, CustomizationStatus.ACTIVE.getValue())
                .orderByAsc(CustomizationOptionEntity::getId);

        if (storeStatus != null) {
            return listOptionsFilteredByStoreStatus(storeId, storeStatus, optionQuery);
        }
        return listOptionsDirectly(storeId, optionQuery);
    }

    /**
     * 按指定门店状态筛选
     */
    private ApiResponse<List<CustomizationOptionDetailResponse>> listOptionsFilteredByStoreStatus(
            Long storeId, Integer requestedStatus,
            LambdaQueryWrapper<CustomizationOptionEntity> optionQuery) {
        if (Objects.equals(StoreCustomizationStatus.DISABLED.getValue(), requestedStatus)) {
            // 售罄是兜底状态，排除有明确非售罄记录的选项
            List<Long> nonDefaultOptionIds = storeOptionRelMapper.selectList(
                    new LambdaQueryWrapper<StoreCustomizationOptionStatusEntity>()
                            .eq(StoreCustomizationOptionStatusEntity::getStoreId, storeId)
                            .ne(StoreCustomizationOptionStatusEntity::getStatus, StoreCustomizationStatus.DISABLED.getValue()))
                    .stream()
                    .map(StoreCustomizationOptionStatusEntity::getOptionId)
                    .toList();
            if (!nonDefaultOptionIds.isEmpty()) {
                optionQuery.notIn(CustomizationOptionEntity::getId, nonDefaultOptionIds);
            }
        } else {
            // 非兜底状态必须有明确的状态记录
            List<Long> matchingOptionIds = storeOptionRelMapper.selectList(
                    new LambdaQueryWrapper<StoreCustomizationOptionStatusEntity>()
                            .eq(StoreCustomizationOptionStatusEntity::getStoreId, storeId)
                            .eq(StoreCustomizationOptionStatusEntity::getStatus, requestedStatus))
                    .stream()
                    .map(StoreCustomizationOptionStatusEntity::getOptionId)
                    .toList();
            if (matchingOptionIds.isEmpty()) {
                return ApiResponse.success(List.of());
            }
            optionQuery.in(CustomizationOptionEntity::getId, matchingOptionIds);
        }

        List<CustomizationOptionEntity> options = optionMapper.selectList(optionQuery);
        return ApiResponse.success(fillOptionStoreStatuses(storeId, options));
    }

    /**
     * 不按门店状态筛选
     */
    private ApiResponse<List<CustomizationOptionDetailResponse>> listOptionsDirectly(
            Long storeId, LambdaQueryWrapper<CustomizationOptionEntity> optionQuery) {
        List<CustomizationOptionEntity> options = optionMapper.selectList(optionQuery);
        return ApiResponse.success(fillOptionStoreStatuses(storeId, options));
    }

    /**
     * 填入门店状态
     */
    private List<CustomizationOptionDetailResponse> fillOptionStoreStatuses(
            Long storeId, List<CustomizationOptionEntity> options) {
        if (options.isEmpty()) {
            return List.of();
        }
        Map<Long, Integer> optionStatusMap = customizationOptionStoreStatusSyncSupport.buildEffectiveStatusMap(storeId, options);
        return options.stream()
                .map(entity -> {
                    int storeStatus = optionStatusMap.getOrDefault(entity.getId(), StoreCustomizationStatus.DISABLED.getValue());
                    return customizationConverter.toOptionDetailResponse(entity, storeStatus);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> updateStatus(Long optionId, UpdateStoreCustomizationOptionStatusRequest request) {
        if (optionMapper.selectById(optionId) == null) {
            return fail(CustomizationErrorCode.OPTION_NOT_FOUND);
        }

        Long storeId = request.getStoreId();
        StoreCustomizationOptionStatusEntity existing = storeOptionRelMapper.selectOne(
                new LambdaQueryWrapper<StoreCustomizationOptionStatusEntity>()
                        .eq(StoreCustomizationOptionStatusEntity::getStoreId, storeId)
                        .eq(StoreCustomizationOptionStatusEntity::getOptionId, optionId));
        if (existing == null) {
            StoreCustomizationOptionStatusEntity statusEntity = StoreCustomizationOptionStatusEntity.builder()
                    .storeId(storeId)
                    .optionId(optionId)
                    .status(request.getStatus())
                    .build();
            if (storeOptionRelMapper.insert(statusEntity) != 1) {
                return fail(CustomizationErrorCode.STORE_OPTION_SALE_STATUS_UPDATE_FAILED);
            }
        } else {
            int rows = storeOptionRelMapper.update(null,
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

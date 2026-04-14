package cn.dextea.product.service.impl;

import cn.dextea.common.util.StringValueUtils;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.dto.request.StorePageQueryCustomizationItemRequest;
import cn.dextea.product.dto.request.UpdateStoreCustomizationItemStatusRequest;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.entity.StoreCustomizationItemStatusEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.StoreCustomizationStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.StoreCustomizationItemStatusMapper;
import cn.dextea.product.service.CustomizationItemBizService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomizationItemBizServiceImpl implements CustomizationItemBizService {

    private final CustomizationItemMapper itemMapper;
    private final StoreCustomizationItemStatusMapper storeItemStatusMapper;
    private final CustomizationConverter customizationConverter;

    /**
     * 分页查询网关
     */
    @Override
    public ApiResponse<IPage<CustomizationItemDetailResponse>> getPage(StorePageQueryCustomizationItemRequest request) {
        Long storeId = request.getStoreId();
        Integer storeStatus = request.getStoreStatus();

        LambdaQueryWrapper<CustomizationItemEntity> query = new LambdaQueryWrapper<CustomizationItemEntity>()
                .eq(request.getGlobalStatus() != null,
                        CustomizationItemEntity::getStatus, request.getGlobalStatus())
                .like(StringValueUtils.hasText(request.getName()),
                        CustomizationItemEntity::getName, StringValueUtils.trim(request.getName()))
                .orderByDesc(CustomizationItemEntity::getId);

        if (storeStatus != null) {
            return getPage(request, storeId, storeStatus, query);
        }
        return getPage(request, storeId, query);
    }

    /**
     * 过滤门店状态
     */
    private ApiResponse<IPage<CustomizationItemDetailResponse>> getPage(
            StorePageQueryCustomizationItemRequest request, Long storeId, Integer targetStatus,
            LambdaQueryWrapper<CustomizationItemEntity> query) {
        if (Objects.equals(StoreCustomizationStatus.DISABLED.getValue(), targetStatus)) {
            // 排除非禁用记录的项目
            List<Long> excludedIds = storeItemStatusMapper.selectList(
                    new LambdaQueryWrapper<StoreCustomizationItemStatusEntity>()
                            .eq(StoreCustomizationItemStatusEntity::getStoreId, storeId)
                            .ne(StoreCustomizationItemStatusEntity::getStatus, StoreCustomizationStatus.DISABLED.getValue()))
                    .stream()
                    .map(StoreCustomizationItemStatusEntity::getItemId)
                    .toList();
            if (!excludedIds.isEmpty()) {
                query.notIn(CustomizationItemEntity::getId, excludedIds);
            }
        } else {
            // 筛选符合要求状态的项目ID
            List<Long> matchingIds = storeItemStatusMapper.selectList(
                    new LambdaQueryWrapper<StoreCustomizationItemStatusEntity>()
                            .eq(StoreCustomizationItemStatusEntity::getStoreId, storeId)
                            .eq(StoreCustomizationItemStatusEntity::getStatus, targetStatus))
                    .stream()
                    .map(StoreCustomizationItemStatusEntity::getItemId)
                    .toList();
            if (matchingIds.isEmpty()) {
                return ApiResponse.success(new Page<>(request.getCurrent(), request.getSize()));
            }
            query.in(CustomizationItemEntity::getId, matchingIds);
        }

        IPage<CustomizationItemEntity> itemPage = itemMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), query);
        return ApiResponse.success(fillStoreStatuses(itemPage, storeId));
    }

    /**
     * 不过滤门店状态
     */
    private ApiResponse<IPage<CustomizationItemDetailResponse>> getPage(
            StorePageQueryCustomizationItemRequest request, Long storeId,
            LambdaQueryWrapper<CustomizationItemEntity> itemQuery) {
        IPage<CustomizationItemEntity> itemPage = itemMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), itemQuery);
        return ApiResponse.success(fillStoreStatuses(itemPage, storeId));
    }

    /**
     * 回填门店状态
     */
    private IPage<CustomizationItemDetailResponse> fillStoreStatuses(
            IPage<CustomizationItemEntity> itemPage, Long storeId) {
        List<CustomizationItemEntity> items = itemPage.getRecords();
        if (items.isEmpty()) {
            return itemPage.convert(
                    entity -> customizationConverter.toItemDetailResponse(
                            entity, StoreCustomizationStatus.DISABLED.getValue()));
        }

        List<Long> itemIds = items.stream().map(CustomizationItemEntity::getId).toList();
        Map<Long, Integer> statusMap = storeItemStatusMapper.selectList(
                new LambdaQueryWrapper<StoreCustomizationItemStatusEntity>()
                        .eq(StoreCustomizationItemStatusEntity::getStoreId, storeId)
                        .in(StoreCustomizationItemStatusEntity::getItemId, itemIds))
                .stream()
                .collect(Collectors.toMap(
                        StoreCustomizationItemStatusEntity::getItemId,
                        StoreCustomizationItemStatusEntity::getStatus,
                        (a, b) -> b));

        return itemPage.convert(entity -> {
            int storeStatus = statusMap.getOrDefault(entity.getId(), StoreCustomizationStatus.DISABLED.getValue());
            return customizationConverter.toItemDetailResponse(entity, storeStatus);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> updateStatus(Long itemId, UpdateStoreCustomizationItemStatusRequest request) {
        if (itemMapper.selectById(itemId) == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        Long storeId = request.getStoreId();
        StoreCustomizationItemStatusEntity existing = storeItemStatusMapper.selectOne(
                new LambdaQueryWrapper<StoreCustomizationItemStatusEntity>()
                        .eq(StoreCustomizationItemStatusEntity::getStoreId, storeId)
                        .eq(StoreCustomizationItemStatusEntity::getItemId, itemId));
        if (existing == null) {
            StoreCustomizationItemStatusEntity statusEntity = StoreCustomizationItemStatusEntity.builder()
                    .storeId(storeId)
                    .itemId(itemId)
                    .status(request.getStatus())
                    .build();
            if (storeItemStatusMapper.insert(statusEntity) != 1) {
                return fail(CustomizationErrorCode.STORE_ITEM_SALE_STATUS_UPDATE_FAILED);
            }
        } else {
            int rows = storeItemStatusMapper.update(null,
                    new LambdaUpdateWrapper<StoreCustomizationItemStatusEntity>()
                            .eq(StoreCustomizationItemStatusEntity::getStoreId, storeId)
                            .eq(StoreCustomizationItemStatusEntity::getItemId, itemId)
                            .set(StoreCustomizationItemStatusEntity::getStatus, request.getStatus()));
            if (rows != 1) {
                return fail(CustomizationErrorCode.STORE_ITEM_SALE_STATUS_UPDATE_FAILED);
            }
        }

        return ApiResponse.success();
    }

    private <T> ApiResponse<T> fail(CustomizationErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}

package cn.dextea.store.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.store.converter.StoreConverter;
import cn.dextea.store.dto.request.NearbyStoreRequest;
import cn.dextea.store.dto.response.NearbyStoreResponse;
import cn.dextea.store.entity.StoreEntity;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.store.service.StoreBizService;
import cn.dextea.store.service.StoreGeoSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreBizServiceImpl implements StoreBizService {

    private final StoreMapper storeMapper;
    private final StoreConverter storeConverter;
    private final StoreGeoSyncService storeGeoSyncService;

    @Override
    public ApiResponse<List<NearbyStoreResponse>> getNearbyStores(NearbyStoreRequest request) {
        // 1. 使用 StoreGeoSyncService 查询附近门店
        List<StoreGeoSyncService.StoreGeoInfo> nearbyStores = storeGeoSyncService.getNearbyStores(
                request.getLongitude(),
                request.getLatitude(),
                request.getRadius(),
                request.getLimit());

        // 2. 如果没有找到附近门店，返回空列表
        if (nearbyStores.isEmpty()) {
            return ApiResponse.success(new ArrayList<>());
        }

        // 3. 提取门店ID列表
        List<Long> storeIds = nearbyStores.stream()
                .map(StoreGeoSyncService.StoreGeoInfo::getStoreId)
                .collect(Collectors.toList());

        // 4. 批量查询门店详细信息
        Map<Long, StoreEntity> storeEntityMap = storeMapper.selectBatchIds(storeIds)
                .stream()
                .collect(Collectors.toMap(StoreEntity::getId, entity -> entity));

        // 5. 组装响应结果，保持 Redis 返回的顺序
        List<NearbyStoreResponse> responseList = new ArrayList<>();
        for (StoreGeoSyncService.StoreGeoInfo storeGeoInfo : nearbyStores) {
            StoreEntity storeEntity = storeEntityMap.get(storeGeoInfo.getStoreId());
            if (storeEntity != null) {
                NearbyStoreResponse nearbyStoreResponse = storeConverter.toNearbyStoreResponse(
                        storeEntity, storeGeoInfo.getDistance());
                responseList.add(nearbyStoreResponse);
            }
        }

        return ApiResponse.success(responseList);
    }
}

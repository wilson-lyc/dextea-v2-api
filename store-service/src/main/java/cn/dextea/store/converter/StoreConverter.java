package cn.dextea.store.converter;

import cn.dextea.store.api.dto.response.StoreValidityResponse;
import cn.dextea.store.dto.response.CreateStoreResponse;
import cn.dextea.store.dto.response.NearbyStoreResponse;
import cn.dextea.store.dto.response.StoreDetailResponse;
import cn.dextea.store.entity.StoreEntity;
import org.springframework.stereotype.Component;

@Component
public class StoreConverter {

    public CreateStoreResponse toCreateStoreResponse(StoreEntity storeEntity) {
        return CreateStoreResponse.builder()
                .id(storeEntity.getId())
                .name(storeEntity.getName())
                .status(storeEntity.getStatus())
                .longitude(storeEntity.getLongitude())
                .latitude(storeEntity.getLatitude())
                .createTime(storeEntity.getCreateTime())
                .build();
    }

    public StoreDetailResponse toStoreDetailResponse(StoreEntity storeEntity) {
        return StoreDetailResponse.builder()
                .id(storeEntity.getId())
                .name(storeEntity.getName())
                .province(storeEntity.getProvince())
                .city(storeEntity.getCity())
                .district(storeEntity.getDistrict())
                .address(storeEntity.getAddress())
                .status(storeEntity.getStatus())
                .longitude(storeEntity.getLongitude())
                .latitude(storeEntity.getLatitude())
                .phone(storeEntity.getPhone())
                .openTime(storeEntity.getOpenTime())
                .createTime(storeEntity.getCreateTime())
                .updateTime(storeEntity.getUpdateTime())
                .build();
    }

    public NearbyStoreResponse toNearbyStoreResponse(StoreEntity storeEntity, Double distance) {
        return NearbyStoreResponse.builder()
                .id(storeEntity.getId())
                .name(storeEntity.getName())
                .province(storeEntity.getProvince())
                .city(storeEntity.getCity())
                .district(storeEntity.getDistrict())
                .address(storeEntity.getAddress())
                .status(storeEntity.getStatus())
                .longitude(storeEntity.getLongitude())
                .latitude(storeEntity.getLatitude())
                .phone(storeEntity.getPhone())
                .openTime(storeEntity.getOpenTime())
                .distance(distance)
                .build();
    }

    public StoreValidityResponse toStoreValidityResponse(Long storeId, boolean valid) {
        return StoreValidityResponse.builder()
                .storeId(storeId)
                .valid(valid)
                .build();
    }
}

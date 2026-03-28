package cn.dextea.store.converter;

import cn.dextea.store.dto.response.NearbyStoreResponse;
import cn.dextea.store.dto.response.StoreDetailResponse;
import cn.dextea.store.entity.StoreEntity;
import org.springframework.stereotype.Component;

@Component
public class StoreConverter {

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
}

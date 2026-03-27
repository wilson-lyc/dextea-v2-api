package cn.dextea.store.service.impl;

import cn.dextea.store.entity.StoreEntity;
import cn.dextea.store.enums.StoreStatus;
import cn.dextea.store.service.StoreGeoSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class StoreGeoSyncServiceImpl implements StoreGeoSyncService {
    private static final String STORE_GEO_KEY = "store:geo";

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void syncStoreLocation(StoreEntity storeEntity) {
        // GEO 同步必须基于已落库的门店 ID 执行，否则无法作为 Redis member 保存。
        if (storeEntity == null || storeEntity.getId() == null) {
            throw new IllegalStateException("Store id is required for geo sync");
        }

        // 关店门店不参与附近门店查询，直接从 GEO 索引中移除。
        if (StoreStatus.CLOSED.getValue() == storeEntity.getStatus()) {
            removeStoreLocation(storeEntity.getId());
            return;
        }

        // Redis GEO 需要有效的经纬度坐标，缺失时直接视为同步失败。
        BigDecimal longitude = storeEntity.getLongitude();
        BigDecimal latitude = storeEntity.getLatitude();
        if (longitude == null || latitude == null) {
            throw new IllegalStateException("Store longitude and latitude are required for geo sync");
        }

        // 使用门店 ID 作为 member，把经纬度写入统一的门店 GEO 集合。
        Long added = redisTemplate.opsForGeo().add(
                STORE_GEO_KEY,
                new Point(longitude.doubleValue(), latitude.doubleValue()),
                buildMember(storeEntity.getId())
        );
        if (added == null) {
            throw new IllegalStateException("Failed to sync store geo location to redis");
        }
    }

    @Override
    public void removeStoreLocation(Long storeId) {
        // 删除 GEO 点位时同样要求传入有效门店 ID。
        if (storeId == null) {
            throw new IllegalStateException("Store id is required for geo removal");
        }

        // 从统一的门店 GEO 集合中移除对应门店 member。
        Long removed = redisTemplate.opsForGeo().remove(STORE_GEO_KEY, buildMember(storeId));
        if (removed == null) {
            throw new IllegalStateException("Failed to remove store geo location from redis");
        }
    }

    private String buildMember(Long storeId) {
        return String.valueOf(storeId);
    }
}

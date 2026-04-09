package cn.dextea.store.service;

import cn.dextea.store.entity.StoreEntity;

import java.util.List;

public interface StoreGeoSyncService {
    void syncStoreLocation(StoreEntity storeEntity);

    void removeStoreLocation(Long storeId);

    /**
     * 查询附近门店
     *
     * @param longitude 用户经度
     * @param latitude  用户纬度
     * @param radius     搜索半径（米）
     * @param limit      返回数量限制
     * @return 附近门店信息列表（按距离升序）
     */
    List<StoreGeoInfo> getNearbyStores(Double longitude, Double latitude, Integer radius, Integer limit);

    /**
     * 门店地理位置信息
     */
    class StoreGeoInfo {
        private final Long storeId;
        private final Double distance;

        public StoreGeoInfo(Long storeId, Double distance) {
            this.storeId = storeId;
            this.distance = distance;
        }

        public Long getStoreId() {
            return storeId;
        }

        public Double getDistance() {
            return distance;
        }
    }
}

package cn.dextea.store.service;

import cn.dextea.store.entity.StoreEntity;

public interface StoreGeoSyncService {
    void syncStoreLocation(StoreEntity storeEntity);

    void removeStoreLocation(Long storeId);
}

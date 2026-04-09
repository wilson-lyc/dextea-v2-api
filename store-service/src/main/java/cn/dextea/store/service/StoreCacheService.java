package cn.dextea.store.service;

import cn.dextea.store.entity.StoreEntity;

public interface StoreCacheService {

    /**
     * 获取门店详情，按 L1 Caffeine → L2 Redis → MySQL 顺序查询并回填。
     * 未找到时返回 null（不缓存空值，避免误判）。
     */
    StoreEntity getStoreDetail(Long id);

    /**
     * 查询门店有效性，按 L1 Caffeine → L2 Redis → MySQL 顺序查询并回填。
     * 门店不存在时缓存 false（短 TTL 防穿透）。
     */
    boolean checkValidity(Long id);

    /**
     * 同步删除门店在 L1 Caffeine 和 L2 Redis 中的详情与有效性缓存。
     */
    void evictStore(Long id);
}

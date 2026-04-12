package cn.dextea.product.service.impl;

import cn.dextea.product.cache.CacheNames;
import cn.dextea.product.cache.TwoLevelCache;
import cn.dextea.product.service.ProductCacheEvictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * Eviction logic for the two-level product service caches.
 *
 * Key formats mirror the SpEL expressions used in {@code @Cacheable} annotations:
 * <ul>
 *   <li>productBizDetail        key: {@code productId:{id}:storeId:{sid}}</li>
 *   <li>menuBiz                 key: {@code store:{sid}}</li>
 *   <li>customizationItemBiz    key: {@code store:{sid}:p:{page}:s:{size}:n:{name}}</li>
 *   <li>customizationOptionsBiz key: {@code item:{iid}:store:{sid}}</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class ProductCacheEvictionServiceImpl implements ProductCacheEvictionService {

    private final CacheManager cacheManager;

    @Override
    public void evictProductBizDetail(Long productId, Long storeId) {
        cache(CacheNames.PRODUCT_BIZ_DETAIL)
                .evict("productId:" + productId + ":storeId:" + storeId);
    }

    @Override
    public void evictProductBizDetailAll(Long productId) {
        cache(CacheNames.PRODUCT_BIZ_DETAIL)
                .evictByKeyPrefix("productId:" + productId + ":");
    }

    @Override
    public void evictProductBizDetailAllClear() {
        cache(CacheNames.PRODUCT_BIZ_DETAIL).clear();
    }

    @Override
    public void evictMenuBizByStore(Long storeId) {
        cache(CacheNames.MENU_BIZ).evict("store:" + storeId);
    }

    @Override
    public void evictMenuBizAll() {
        cache(CacheNames.MENU_BIZ).clear();
    }

    @Override
    public void evictCustomizationItemBizAll() {
        cache(CacheNames.CUSTOMIZATION_ITEM_BIZ).clear();
    }

    @Override
    public void evictCustomizationOptionsBizByItem(Long itemId) {
        cache(CacheNames.CUSTOMIZATION_OPTIONS_BIZ)
                .evictByKeyPrefix("item:" + itemId + ":");
    }

    @Override
    public void evictCustomizationOptionsBizAll() {
        cache(CacheNames.CUSTOMIZATION_OPTIONS_BIZ).clear();
    }

    private TwoLevelCache cache(String name) {
        return (TwoLevelCache) cacheManager.getCache(name);
    }
}

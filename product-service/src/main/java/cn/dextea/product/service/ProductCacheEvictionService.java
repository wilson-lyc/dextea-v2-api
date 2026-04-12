package cn.dextea.product.service;

/**
 * Handles targeted cache eviction for the product service.
 * Called by both admin and biz service impls after write operations.
 */
public interface ProductCacheEvictionService {

    /** Evict the biz product detail for a specific product+store pair. */
    void evictProductBizDetail(Long productId, Long storeId);

    /** Evict all biz product detail entries for a product across every store. */
    void evictProductBizDetailAll(Long productId);

    /** Clear the entire biz product detail cache (used when affected productIds are unknown). */
    void evictProductBizDetailAllClear();

    /** Evict the biz menu for a store. */
    void evictMenuBizByStore(Long storeId);

    /** Evict all biz menus (used when menu structure changes and affected stores are unknown). */
    void evictMenuBizAll();

    /** Evict all biz customization-item list pages. */
    void evictCustomizationItemBizAll();

    /** Evict all biz customization-option list entries for a specific item. */
    void evictCustomizationOptionsBizByItem(Long itemId);

    /** Evict all biz customization-option list entries. */
    void evictCustomizationOptionsBizAll();
}

package cn.dextea.product.service;

import cn.dextea.common.pojo.Product;

/**
 * @author Lai Yongchao
 */
public interface InternalService {
    boolean isProductIdValid(Long id);
    boolean isCategoryIdValid(Long id);
    boolean isCustomizeItemIdValid(Long id);

    Product getProductById(Long productId);
    Product getProductById(Long productId, Long storeId);
    Integer getProductStoreStatus(Long productId, Long storeId);
    Integer getProductGlobalStatus(Long productId);
    // 选项
    boolean isCustomizeOptionIdValid(Long id);
    Integer getCustomizeOptionGlobalStatus(Long optionId);
    Integer getCustomizeOptionStoreStatus(Long optionId, Long storeId);
}

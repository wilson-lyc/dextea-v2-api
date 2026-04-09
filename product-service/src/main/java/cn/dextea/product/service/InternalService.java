package cn.dextea.product.service;

import cn.dextea.common.model.product.ProductModel;

import java.math.BigDecimal;

/**
 * @author Lai Yongchao
 */
public interface InternalService {
    boolean isProductIdValid(Long id);
    boolean isCategoryIdValid(Long id);
    boolean isCustomizeItemIdValid(Long id);

    ProductModel getProductDetail(Long productId);
    ProductModel getProductDetail(Long productId, Long storeId);
    Integer getProductStoreStatus(Long productId, Long storeId);
    Integer getProductGlobalStatus(Long productId);
    // 选项
    boolean isCustomizeOptionIdValid(Long id);
    Integer getOptionGlobalStatus(Long optionId);
    Integer getOptionStoreStatus(Long optionId, Long storeId);

    BigDecimal getCustomizeOptionPrice(Long id);
    BigDecimal getProductPrice(Long id);

    ProductModel getProductBase(Long productId);
    ProductModel getProductBase(Long productId, Long storeId);
}

package cn.dextea.product.service;

/**
 * @author Lai Yongchao
 */
public interface InternalService {
    boolean isProductIdValid(Long id);
    boolean isCategoryIdValid(Long id);
    boolean isCustomizeItemIdValid(Long id);
    boolean isCustomizeOptionIdValid(Long id);
}

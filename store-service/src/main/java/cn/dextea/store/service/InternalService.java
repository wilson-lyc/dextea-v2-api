package cn.dextea.store.service;

import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface InternalService {
    boolean isStoreIdValid(Long id);
    String getStoreName(Long id) throws IllegalArgumentException;
    boolean storeBindMenu(Long storeId, Long menuId) throws NotFoundException;
    Long getStoreMenuId(Long id);
    String getStorePhone(Long id);
}

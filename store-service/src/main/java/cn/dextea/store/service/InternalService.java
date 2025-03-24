package cn.dextea.store.service;

/**
 * @author Lai Yongchao
 */
public interface InternalService {
    boolean isStoreIdValid(Long id);
    String getStoreName(Long id) throws IllegalArgumentException;
}

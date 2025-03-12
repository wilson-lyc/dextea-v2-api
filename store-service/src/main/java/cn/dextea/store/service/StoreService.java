package cn.dextea.store.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.dto.*;

/**
 * @author Lai Yongchao
 */
public interface StoreService {
    // 创建
    ApiResponse createStore(StoreCreateDTO data);
    // 列表
    ApiResponse getStoreList(int current, int size, StoreFilter filter);
    ApiResponse getStoreOption(Integer status);
    // 单项
    ApiResponse getStoreBase(Long id);
    ApiResponse getStoreLicense(Long id);
    ApiResponse getStoreStatus(Long id);
    ApiResponse getStoreLocation(Long id);
    // 更新
    ApiResponse updateStoreBase(Long id, StoreUpdateBaseDTO data);
    ApiResponse updateStoreLocation(Long id, StoreUpdateLocationDTO body);
    ApiResponse updateStoreStatus(Long id, StoreUpdateStatusDTO status);
}

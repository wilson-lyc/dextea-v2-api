package cn.dextea.store.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.dto.*;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface StoreService {
    // 创建
    ApiResponse createStore(StoreCreateDTO data);
    // 列表
    ApiResponse getStoreList(int current, int size, StoreFilter filter);
    ApiResponse getStoreOption(String status);
    ApiResponse getStoreTreeOption();
    // 单项
    ApiResponse getStoreBase(Long id) throws NotFoundException;
    ApiResponse getStoreLicense(Long id) throws NotFoundException;
    ApiResponse getStoreStatus(Long id) throws NotFoundException;
    ApiResponse getStoreLocation(Long id) throws NotFoundException;
    // 更新
    ApiResponse updateStoreBase(Long id, StoreUpdateBaseDTO data) throws NotFoundException;
    ApiResponse updateStoreLocation(Long id, StoreUpdateLocationDTO body) throws NotFoundException;
    ApiResponse updateStoreStatus(Long id, Integer status) throws NotFoundException;
}

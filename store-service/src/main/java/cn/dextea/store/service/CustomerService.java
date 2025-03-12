package cn.dextea.store.service;

import cn.dextea.common.dto.ApiResponse;

/**
 * @author Lai Yongchao
 */
public interface CustomerService {
    ApiResponse getNearbyStore(Double longitude, Double latitude, Integer radius, Integer limit);
    ApiResponse getStoreInfo(Long id, Double longitude, Double latitude);
    ApiResponse getStoreMenu(Long id);
}

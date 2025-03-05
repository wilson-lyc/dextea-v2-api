package cn.dextea.store.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.dto.StoreCreateDTO;
import cn.dextea.store.dto.StoreFilter;
import cn.dextea.store.dto.StoreUpdateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * @author Lai Yongchao
 */
public interface StoreService {
    ApiResponse getStoreList(int current, int size, StoreFilter filter);
    ApiResponse createStore(StoreCreateDTO data);
    ApiResponse getStoreById(Long id);
    ApiResponse updateStatus(Long id, Integer status);
    ApiResponse updateBase(Long id, StoreUpdateDTO data);
    ResponseEntity<ApiResponse> uploadBusinessLicense(Long id, MultipartFile file);
    ResponseEntity<ApiResponse> uploadFoodLicense(Long id, MultipartFile file);
    ApiResponse getSelectOptions();
    ApiResponse updateLocation(Long id, BigDecimal longitude, BigDecimal latitude);
}

package cn.dextea.store.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.dto.CreateStoreDTO;
import cn.dextea.store.dto.SearchStoreDTO;
import cn.dextea.store.dto.UpdateStoreDTO;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lai Yongchao
 */
public interface StoreService {
    ApiResponse getStoreList(int current, int size, SearchStoreDTO filter);
    ApiResponse create(CreateStoreDTO data);
    ApiResponse getStoreById(Long id);
    ApiResponse updateStatus(Long id, Integer status);
    ApiResponse update(Long id, UpdateStoreDTO data);
    ResponseEntity<ApiResponse> uploadBusinessLicense(Long id, MultipartFile file);
    ResponseEntity<ApiResponse> uploadFoodLicense(Long id, MultipartFile file);
    ApiResponse getLicenseById(Long id);
    ApiResponse getSelectOptions();
}

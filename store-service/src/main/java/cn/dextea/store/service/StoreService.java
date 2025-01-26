package cn.dextea.store.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.dto.CreateStoreDTO;
import cn.dextea.store.dto.SearchStoreDTO;
import cn.dextea.store.dto.UpdateStoreDTO;
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
    ApiResponse uploadBusinessLicense(Long id, MultipartFile file);
    ApiResponse uploadFoodLicense(Long id, MultipartFile file);
}

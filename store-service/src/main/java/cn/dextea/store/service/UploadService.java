package cn.dextea.store.service;

import cn.dextea.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lai Yongchao
 */
public interface UploadService {
    ResponseEntity<ApiResponse> uploadBusinessLicense(Long id, MultipartFile file);
    ResponseEntity<ApiResponse> uploadFoodLicense(Long id, MultipartFile file);
}

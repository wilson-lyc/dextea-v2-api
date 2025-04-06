package cn.dextea.store.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.store.model.UploadResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lai Yongchao
 */
public interface UploadService {
    ResponseEntity<DexteaApiResponse<UploadResponse>> uploadBusinessLicense(Long id, MultipartFile file);
    ResponseEntity<DexteaApiResponse<UploadResponse>> uploadFoodLicense(Long id, MultipartFile file);
}

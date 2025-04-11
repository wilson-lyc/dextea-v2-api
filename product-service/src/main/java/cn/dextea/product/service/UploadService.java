package cn.dextea.product.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.product.model.UploadResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lai Yongchao
 */
public interface UploadService {
    ResponseEntity<DexteaApiResponse<UploadResponse>> uploadCover(Long id, MultipartFile file);
    ResponseEntity<DexteaApiResponse<UploadResponse>> uploadDetailHeaderImg(Long id, MultipartFile file);
}

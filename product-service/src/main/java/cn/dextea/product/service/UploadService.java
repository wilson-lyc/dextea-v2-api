package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lai Yongchao
 */
public interface UploadService {
    ResponseEntity<ApiResponse> uploadCover(Long id, MultipartFile file);
    ResponseEntity<ApiResponse> uploadDetailHeaderImg(Long id, MultipartFile file);
}

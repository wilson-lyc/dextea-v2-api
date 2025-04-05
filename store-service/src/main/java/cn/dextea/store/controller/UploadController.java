package cn.dextea.store.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.store.dto.UploadResponse;
import cn.dextea.store.service.UploadService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lai Yongchao
 */
@RestController
public class UploadController {
    @Resource
    private UploadService uploadService;

    /**
     * 上传营业执照
     * @param id 门店id
     * @param file 营业执照文件
     */
    @PostMapping(value = "/store/upload/business-license", consumes = "multipart/form-data")
    public ResponseEntity<DexteaApiResponse<UploadResponse>> uploadBusinessLicense(
            @RequestParam Long id,
            @RequestPart MultipartFile file) {
        return uploadService.uploadBusinessLicense(id, file);
    }

    /**
     * 上传食品经营许可证
     * @param id 门店id
     * @param file 食品经营许可证
     */
    @PostMapping(value = "/store/upload/food-license", consumes = "multipart/form-data")
    public ResponseEntity<DexteaApiResponse<UploadResponse>> uploadFoodLicense(
            @RequestParam Long id,
            @RequestPart MultipartFile file) {
        return uploadService.uploadFoodLicense(id, file);
    }
}

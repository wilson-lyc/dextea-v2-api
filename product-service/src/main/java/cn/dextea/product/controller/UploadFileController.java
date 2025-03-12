package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.service.UploadService;
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
public class UploadFileController {
    @Resource
    private UploadService uploadService;
    /**
     * 上传商品封面
     * @param id 商品id
     * @param file 封面图
     */
    @PostMapping(value = "/product/upload/cover", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> uploadCover(@RequestParam Long id, @RequestPart MultipartFile file) {
        return uploadService.uploadCover(id, file);
    }
    @PostMapping(value="/product/upload/detailHeaderImg", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> uploadDetailHeaderImg(@RequestParam Long id, @RequestPart MultipartFile file) {
        return uploadService.uploadDetailHeaderImg(id, file);
    }
}

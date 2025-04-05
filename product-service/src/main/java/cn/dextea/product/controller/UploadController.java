package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.product.dto.UploadResponse;
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
public class UploadController {
    @Resource
    private UploadService uploadService;
    /**
     * 上传商品封面
     * @param id 商品id
     * @param file 封面图
     */
    @PostMapping(value = "/product/upload/cover", consumes = "multipart/form-data")
    public ResponseEntity<DexteaApiResponse<UploadResponse>> uploadCover(
            @RequestParam Long id,
            @RequestPart MultipartFile file) {
        return uploadService.uploadCover(id, file);
    }

    /**
     * 上传商品详情页头部图
     * @param id 商品ID
     * @param file 文件
     */
    @PostMapping(value="/product/upload/detail-header-img", consumes = "multipart/form-data")
    public ResponseEntity<DexteaApiResponse<UploadResponse>> uploadDetailHeaderImg(
            @RequestParam Long id,
            @RequestPart MultipartFile file) {
        return uploadService.uploadDetailHeaderImg(id, file);
    }
}

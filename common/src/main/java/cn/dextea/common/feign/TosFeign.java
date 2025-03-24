package cn.dextea.common.feign;

import cn.dextea.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient("tos-service")
public interface TosFeign {
    @PostMapping(value = "/tos/originalName", consumes = "multipart/form-data")
    ApiResponse uploadWithOriginalName(@RequestParam("folder") String folder, @RequestPart("file") MultipartFile file);
    @PostMapping(value = "/tos/customName", consumes = "multipart/form-data")
    ApiResponse uploadWithCustomName(@RequestParam("folder") String folder, @RequestParam("filename") String fileName, @RequestPart("file") MultipartFile file);
    @DeleteMapping("/tos")
    ApiResponse delete(@RequestParam("url") String url);
}

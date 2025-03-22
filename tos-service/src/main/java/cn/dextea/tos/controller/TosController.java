package cn.dextea.tos.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.tos.service.TosService;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TosController {
    @Autowired
    private TosService tosService;

    /**
     * 上传文件
     * @param folder 文件夹
     * @param file 文件
     */
    @PostMapping(value = "/tos/originalName",consumes ="multipart/form-data")
    public ApiResponse uploadFileOriginalName(@RequestParam String folder, @RequestPart MultipartFile file) {
        return tosService.uploadFile(folder, file);
    }

    /**
     * 上传文件
     * @param folder 文件夹
     * @param filename 文件名
     * @param file 文件
     */
    @PostMapping(value = "/tos/customName",consumes ="multipart/form-data")
    public ApiResponse uploadFileCustomName(@RequestParam String folder, @RequestParam String filename, @RequestPart MultipartFile file) {
        String url= tosService.uploadFile(folder, filename, file);
        return ApiResponse.success(JSONObject.of("url",url));
    }

    @DeleteMapping("/tos")
    public boolean delete(@RequestParam String url) {
        return tosService.delete(url);
    }

    @PostMapping(value = "/tos",consumes ="multipart/form-data")
    public String uploadFile(
            @RequestParam String folder,
            @RequestParam String filename,
            @RequestPart MultipartFile file) {
        return tosService.uploadFile(folder,filename,file);
    }
}

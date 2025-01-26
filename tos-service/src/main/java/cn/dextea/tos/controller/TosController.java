package cn.dextea.tos.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.tos.service.TosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/tos")
public class TosController {
    @Autowired
    private TosService tosService;

    /**
     * 上传文件
     * @param folder 文件夹
     * @param file 文件
     * @return ApiResponse
     */
    @PostMapping(value = "/originalName",consumes ="multipart/form-data")
    public ApiResponse uploadFileOriginalName(@RequestParam String folder, @RequestPart MultipartFile file) {
        return tosService.uploadFile(folder, file);
    }
    @PostMapping(value = "/customName",consumes ="multipart/form-data")
    public ApiResponse uploadFileCustomName(@RequestParam String folder, @RequestParam String filename, @RequestPart MultipartFile file) {
        return tosService.uploadFile(folder, filename, file);
    }
}

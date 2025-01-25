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
     * @param key 文件名
     * @param file 文件
     * @return ApiResponse
     */
    @PostMapping(value = "",consumes ="multipart/form-data")
    public ApiResponse uploadFile(@RequestParam String folder, @RequestPart MultipartFile file) {
        return tosService.uploadFile(folder, file);
    }
}

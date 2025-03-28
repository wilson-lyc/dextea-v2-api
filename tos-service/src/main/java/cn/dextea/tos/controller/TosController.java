package cn.dextea.tos.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.tos.service.TosService;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TosController {
    @Resource
    private TosService tosService;

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

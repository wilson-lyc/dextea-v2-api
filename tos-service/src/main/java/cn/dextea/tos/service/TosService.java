package cn.dextea.tos.service;

import cn.dextea.common.dto.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

public interface TosService {
    ApiResponse uploadFile(String folder, MultipartFile file);
    String uploadFile(String folder, String filename, MultipartFile file);
    boolean delete(String url);
}

package cn.dextea.tos.service;

import cn.dextea.common.dto.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

public interface TosService {
    String uploadFile(String folder, String filename, MultipartFile file);
    boolean delete(String url);
}

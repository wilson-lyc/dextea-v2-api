package cn.dextea.tos.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.tos.service.TosService;
import cn.dextea.tos.util.TosUtil;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TosServiceImpl implements TosService {
    @Autowired
    private TosUtil tosUtil;

    @Override
    public ApiResponse uploadFile(String folder, MultipartFile file) {
        String url = tosUtil.uploadMultipartFile(folder,file);
        if (url != null) {
            return ApiResponse.success(JSONObject.of("url",url));
        }
        return ApiResponse.badRequest("文件上传失败");
    }
}

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
    public String uploadFile(String folder, String filename, MultipartFile file) {
        try{
            return tosUtil.uploadMultipartFile(folder,filename,file);
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public boolean delete(String url) {
        return tosUtil.delete(url);
    }
}

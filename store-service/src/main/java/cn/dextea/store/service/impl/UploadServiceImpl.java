package cn.dextea.store.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.feign.TosFeign;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.common.pojo.Store;
import cn.dextea.store.service.UploadService;
import com.alibaba.fastjson2.JSONObject;
import com.google.protobuf.Api;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class UploadServiceImpl implements UploadService {
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private TosFeign tosFeign;

    @Override
    public ResponseEntity<ApiResponse> uploadBusinessLicense(Long id, MultipartFile file) {
        // 获取旧url
        String oldUrl=storeMapper.selectById(id).getBusinessLicense();
        if(Objects.nonNull(oldUrl)) {
            if (!tosFeign.delete(oldUrl))
                return ResponseEntity.internalServerError().body(ApiResponse.serverError("上传服务异常"));
        }
        // 上传
        String folder="store/license";
        String filename=String.format("%d_business",id);
        String url;
        try{
            url=tosFeign.uploadFile(folder,filename,file);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("上传失败"));
        }
        // 更新db
        Store store=Store.builder()
                .id(id)
                .businessLicense(url)
                .build();
        storeMapper.updateById(store);
        return ResponseEntity.ok(ApiResponse.success("上传成功", JSONObject.of("url",url)));
    }

    @Override
    public ResponseEntity<ApiResponse> uploadFoodLicense(Long id, MultipartFile file) {
        // 获取旧url
        String oldUrl=storeMapper.selectById(id).getFoodLicense();
        if(Objects.nonNull(oldUrl)) {
            if (!tosFeign.delete(oldUrl))
                return ResponseEntity.internalServerError().body(ApiResponse.serverError("上传服务异常"));
        }
        // 上传
        String folder="store/license";
        String filename=String.format("%d_food",id);
        String url;
        try{
            url=tosFeign.uploadFile(folder,filename,file);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("上传失败"));
        }
        // 更新db
        Store store=Store.builder()
                .id(id)
                .foodLicense(url)
                .build();
        storeMapper.updateById(store);
        return ResponseEntity.ok(ApiResponse.success("上传成功", JSONObject.of("url",url)));
    }
}

package cn.dextea.store.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.feign.TosFeign;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.common.pojo.Store;
import cn.dextea.store.service.UploadService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
        // 查询旧的营业执照URL
        String oldUrl=storeMapper.selectById(id).getBusinessLicense();
        tosFeign.delete(oldUrl);
        // 上传新的营业执照
        String folder="store/license";
        String filename=String.format("%d_business",id);
        ApiResponse response=tosFeign.uploadWithCustomName(folder,filename,file);
        if (response.getCode()==200){
            String url=response.getData().getString("url");
            Store store=Store.builder()
                    .id(id)
                    .businessLicense(url)
                    .build();
            storeMapper.updateById(store);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(ApiResponse.badRequest("上传失败"));
    }

    @Override
    public ResponseEntity<ApiResponse> uploadFoodLicense(Long id, MultipartFile file) {
        // 删除旧的食品经营许可证
        String oldUrl=storeMapper.selectById(id).getFoodLicense();
        tosFeign.delete(oldUrl);
        // 上传新的食品经营许可证
        String folder="store/license";
        String filename=String.format("%d_food",id);
        ApiResponse response=tosFeign.uploadWithCustomName(folder,filename,file);
        if (response.getCode()==200){
            String url=response.getData().getString("url");
            Store store=Store.builder()
                    .id(id)
                    .foodLicense(url)
                    .build();
            storeMapper.updateById(store);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(ApiResponse.badRequest("上传失败"));
    }
}

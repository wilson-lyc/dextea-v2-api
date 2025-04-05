package cn.dextea.store.service.impl;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.common.exception.TosUtilException;
import cn.dextea.common.util.TosUtil;
import cn.dextea.store.dto.UploadResponse;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.store.pojo.Store;
import cn.dextea.store.service.UploadService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Slf4j
@Service
public class UploadServiceImpl implements UploadService {
    @Resource
    private StoreMapper storeMapper;
    @Resource
    private TosUtil tosUtil;

    @Override
    public ResponseEntity<DexteaApiResponse<UploadResponse>> uploadBusinessLicense(Long id, MultipartFile file) {
        // 校验ID
        if(Objects.isNull(storeMapper.selectById(id))){
            return ResponseEntity.badRequest().body(DexteaApiResponse.fail("id错误"));
        }
        // 文件夹
        String folder="/store/license";
        // 文件名 e.g: 1_business
        String filename=String.format("%d_business",id);
        String url;
        // 上传文件
        try {
            url = tosUtil.uploadFile(folder,filename,file);
        } catch (TosUtilException e) {
            log.error("营业执照上传失败",e.getCause());
            return ResponseEntity.internalServerError().body(DexteaApiResponse.serverError("文件上传失败"));
        }
        // 更新数据库
        LambdaUpdateWrapper<Store> wrapper=new LambdaUpdateWrapper<Store>()
                .set(Store::getBusinessLicense,url)
                .eq(Store::getId,id);
        storeMapper.update(wrapper);
        return ResponseEntity.ok(DexteaApiResponse.success("上传成功",new UploadResponse(url)));
    }

    @Override
    public ResponseEntity<DexteaApiResponse<UploadResponse>> uploadFoodLicense(Long id, MultipartFile file) {
        // 校验ID
        if(Objects.isNull(storeMapper.selectById(id))){
            return ResponseEntity.badRequest().body(DexteaApiResponse.fail("id错误"));
        }
        // 文件夹
        String folder="/store/license";
        // 文件名 e.g: 1_food
        String filename=String.format("%d_food",id);
        String url;
        // 上传文件
        try{
            url=tosUtil.uploadFile(folder,filename,file);
        }catch (Exception e){
            log.error("食品经营许可证上传失败",e.getCause());
            return ResponseEntity.internalServerError().body(DexteaApiResponse.serverError("文件上传失败"));
        }
        // 更新数据库
        LambdaUpdateWrapper<Store> wrapper=new LambdaUpdateWrapper<Store>()
                .set(Store::getFoodLicense,url)
                .eq(Store::getId,id);
        storeMapper.update(wrapper);
        return ResponseEntity.ok(DexteaApiResponse.success("上传成功",new UploadResponse(url)));
    }
}

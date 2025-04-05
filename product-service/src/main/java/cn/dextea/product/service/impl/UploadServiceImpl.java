package cn.dextea.product.service.impl;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.common.util.TosUtil;
import cn.dextea.product.dto.UploadResponse;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.service.UploadService;
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
    private TosUtil tosUtil;
    @Resource
    private ProductMapper productMapper;

    @Override
    public ResponseEntity<DexteaApiResponse<UploadResponse>> uploadCover(Long id, MultipartFile file) {
        // 校验ID
        if(Objects.isNull(productMapper.selectById(id))){
            return ResponseEntity.badRequest().body(DexteaApiResponse.fail("id错误"));
        }
        // 文件夹
        String folder=String.format("/product/%d",id);
        // 文件名 e.g: 1_cover
        String filename=String.format("%d_cover",id);
        String url;
        // 上传文件
        try{
            url=tosUtil.uploadFile(folder,filename,file);
        }catch (Exception e){
            log.error("封面图上传失败",e.getCause());
            return ResponseEntity.internalServerError().body(DexteaApiResponse.serverError("文件上传失败"));
        }
        // 更新数据库
        LambdaUpdateWrapper<Product> wrapper=new LambdaUpdateWrapper<Product>()
                .set(Product::getCover,url)
                .eq(Product::getId,id);
        productMapper.update(wrapper);
        return ResponseEntity.ok(DexteaApiResponse.success("上传成功",new UploadResponse(url)));
    }

    @Override
    public ResponseEntity<DexteaApiResponse<UploadResponse>> uploadDetailHeaderImg(Long id, MultipartFile file) {
        // 校验ID
        if(Objects.isNull(productMapper.selectById(id))){
            return ResponseEntity.badRequest().body(DexteaApiResponse.fail("id错误"));
        }
        // 文件夹
        String folder=String.format("/product/%d",id);
        // 文件名 e.g: 1_detail_header_img
        String filename=String.format("%d_detail_header_img",id);
        String url;
        // 上传文件
        try{
            url=tosUtil.uploadFile(folder,filename,file);
        }catch (Exception e){
            log.error("详情页头图上传失败",e.getCause());
            return ResponseEntity.internalServerError().body(DexteaApiResponse.serverError("文件上传失败"));
        }
        // 更新数据库
        LambdaUpdateWrapper<Product> wrapper=new LambdaUpdateWrapper<Product>()
                .set(Product::getDetailHeaderImg,url)
                .eq(Product::getId,id);
        productMapper.update(wrapper);
        return ResponseEntity.ok(DexteaApiResponse.success("上传成功",new UploadResponse(url)));
    }
}

package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.feign.TosFeign;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.common.pojo.Product;
import cn.dextea.product.service.UploadService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
    private TosFeign tosFeign;

    @Resource
    private ProductMapper productMapper;

    @Override
    public ResponseEntity<ApiResponse> uploadCover(Long id, MultipartFile file) {
        // 删除旧封面
        QueryWrapper<Product> wrapper=new QueryWrapper<Product>().eq("id",id);
        String oldUrl=productMapper.selectOne(wrapper).getCover();
        if(Objects.nonNull(oldUrl)) {
            if (!tosFeign.delete(oldUrl))
                return ResponseEntity.internalServerError().body(ApiResponse.serverError("上传服务异常"));
        }
        // 上传新封面
        String folder=String.format("product/%d",id);
        String filename=String.format("%d_cover",id);
        String url=null;
        try{
            url=tosFeign.uploadFile(folder,filename,file);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("上传失败"));
        }
        // 更新db
        UpdateWrapper<Product> updateWrapper=new UpdateWrapper<Product>()
                .eq("id",id)
                .set("cover",url);
        productMapper.update(updateWrapper);
        return ResponseEntity.ok(ApiResponse.success("上传成功", JSONObject.of("url",url)));
    }

    @Override
    public ResponseEntity<ApiResponse> uploadDetailHeaderImg(Long id, MultipartFile file) {
        // 删除旧图
        QueryWrapper<Product> wrapper=new QueryWrapper<Product>().eq("id",id);
        String oldUrl=productMapper.selectOne(wrapper).getDetailHeaderImg();
        if(Objects.nonNull(oldUrl)) {
            if (!tosFeign.delete(oldUrl))
                return ResponseEntity.internalServerError().body(ApiResponse.serverError("上传服务异常"));
        }
        // 上传新图
        String folder=String.format("product/%d",id);
        String filename=String.format("%d_detail_header_img",id);
        String url;
        try{
            url=tosFeign.uploadFile(folder,filename,file);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("上传失败"));
        }
        // 更新db
        UpdateWrapper<Product> updateWrapper=new UpdateWrapper<Product>()
                .eq("id",id)
                .set("detail_header_img",url);
        productMapper.update(updateWrapper);
        return ResponseEntity.ok(ApiResponse.success("上传成功", JSONObject.of("url",url)));
    }
}

package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.feign.TosFeign;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.service.UploadService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
        // 删除旧的封面
        QueryWrapper<Product> wrapper=new QueryWrapper<Product>().eq("id",id);
        String oldUrl=productMapper.selectOne(wrapper).getCover();
        try{
            tosFeign.delete(oldUrl);
        }catch (Exception e){
            log.error("删除封面失败",e);
        }
        // 上传新封面
        String folder=String.format("product/%d",id);
        String filename=String.format("%d_cover",id);
        ApiResponse response=tosFeign.uploadWithCustomName(folder,filename,file);
        if (response.getCode()==200){
            String url=response.getData().getString("url");
            Product store=Product.builder()
                    .id(id)
                    .cover(url)
                    .build();
            productMapper.updateById(store);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(ApiResponse.badRequest("上传失败"));
    }

    @Override
    public ResponseEntity<ApiResponse> uploadDetailHeaderImg(Long id, MultipartFile file) {
        // 删除旧图
        QueryWrapper<Product> wrapper=new QueryWrapper<Product>().eq("id",id);
        String oldUrl=productMapper.selectOne(wrapper).getDetailHeaderImg();
        try{
            tosFeign.delete(oldUrl);
        }catch (Exception e){
            log.error("删除详情页头图失败",e);
        }
        // 上传新封面
        String folder=String.format("product/%d",id);
        String filename=String.format("%d_detail_header_img",id);
        ApiResponse response=tosFeign.uploadWithCustomName(folder,filename,file);
        if (response.getCode()==200){
            String url=response.getData().getString("url");
            Product store=Product.builder()
                    .id(id)
                    .detailHeaderImg(url)
                    .build();
            productMapper.updateById(store);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(ApiResponse.badRequest("上传失败"));
    }
}

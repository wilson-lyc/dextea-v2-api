package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.*;
import cn.dextea.product.feign.TosFeign;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.pojo.ProductType;
import cn.dextea.product.service.ProductService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private TosFeign tosFeign;

    @Override
    public ApiResponse create(ProductCreateDTO data) {
        Product product = data.toProduct();
        productMapper.insert(product);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse getProductById(Long id) {
        Product product = productMapper.selectById(id);
        if(product == null) {
            return ApiResponse.notFound(String.format("商品不存在，id=%d", id));
        }
        return ApiResponse.success(JSONObject.of("product", product));
    }

    @Override
    public ApiResponse getProductListByFilter(int current, int size, SearchProductDTO filter) {
        // 联表查询
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAll(Product.class,Product::getTypeId)
                .selectAs(ProductType::getName, ProductListDTO::getTypeName)
                .innerJoin(ProductType.class, ProductType::getId, Product::getTypeId)
                .orderByAsc(Product::getId);
        // 添加过滤条件
        if (filter.getId() != null) {
            wrapper.eq("id", filter.getId());
        }
        if (filter.getName() != null && !filter.getName().isBlank()) {
            wrapper.like("name", filter.getName());
        }
        if (filter.getTypeId() != null) {
            wrapper.eq("type_id", filter.getTypeId());
        }
        if (filter.getStatus() != null) {
            wrapper.eq("status", filter.getStatus());
        }
        // 分页查询
        IPage<ProductListDTO> page=productMapper.selectJoinPage(new Page<>(current, size), ProductListDTO.class,wrapper);
        // 如果当前页码大于总页数，返回最后一页
        if (page.getCurrent() > page.getPages()) {
            page = productMapper.selectJoinPage(new Page<>(page.getPages(), size), ProductListDTO.class,wrapper);
        }
        return ApiResponse.success(JSONObject.from(page));
    }

    @Override
    public ResponseEntity<ApiResponse> uploadCover(Long id, MultipartFile file) {
        // 删除旧的封面
        QueryWrapper<Product> wrapper=new QueryWrapper<>();
        wrapper.eq("id",id);
        String oldUrl=productMapper.selectOne(wrapper).getCover();
        try{
            tosFeign.delete(oldUrl);
        }catch (Exception e){
            log.error("删除封面失败",e);
        }
        // 上传封面
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
    public ApiResponse getProductOption(Integer status) {
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAs(Product::getId, ProductOptionDTO::getValue)
                .selectAs(Product::getName, ProductOptionDTO::getLabel);
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        List<ProductOptionDTO> list = productMapper.selectJoinList(ProductOptionDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("options", list));
    }
}

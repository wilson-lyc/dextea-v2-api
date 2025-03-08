package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.*;
import cn.dextea.product.feign.TosFeign;
import cn.dextea.product.mapper.CustomizeItemMapper;
import cn.dextea.product.mapper.CustomizeOptionMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.pojo.CustomizeItem;
import cn.dextea.product.pojo.CustomizeOption;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.pojo.ProductCategory;
import cn.dextea.product.service.ProductService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import jdk.jfr.Category;
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
    @Resource
    private ProductMapper productMapper;

    @Resource
    private TosFeign tosFeign;

    @Resource
    private CustomizeItemMapper customizeItemMapper;

    @Resource
    private CustomizeOptionMapper customizeOptionMapper;

    @Override
    public ApiResponse createProduct(ProductCreateDTO data) {
        Product product = data.toProduct();
        productMapper.insert(product);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse getProductBaseById(Long id) {
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class,ProductDTO.class)
                .selectAs(ProductCategory::getName, ProductListDTO::getCategoryName)
                .innerJoin(ProductCategory.class,ProductCategory::getId,Product::getCategoryId)
                .eq(Product::getId,id);
        ProductDTO product = productMapper.selectJoinOne(ProductDTO.class,wrapper);
        if(product == null) {
            return ApiResponse.notFound(String.format("不存在id=%d的商品", id));
        }
        return ApiResponse.success(JSONObject.of("product", product));
    }

    @Override
    public ApiResponse getProductList(int current, int size, ProductQueryDTO filter) {
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class,ProductListDTO.class)
                .selectAs(ProductCategory::getName, ProductListDTO::getCategoryName)
                .innerJoin(ProductCategory.class, ProductCategory::getId, Product::getCategoryId)
                .orderByAsc(Product::getId);
        // 添加过滤条件
        if (filter.getId() != null) {
            wrapper.eq(Product::getId, filter.getId());
        }
        if (filter.getName() != null && !filter.getName().isBlank()) {
            wrapper.like(Product::getName, filter.getName());
        }
        if (filter.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, filter.getCategoryId());
        }
        if (filter.getStatus() != null) {
            wrapper.eq(Product::getStatus, filter.getStatus());
        }
        // 分页查询
        IPage<ProductListDTO> page=productMapper.selectJoinPage(
                new Page<>(current, size),
                ProductListDTO.class,
                wrapper);
        if (page.getCurrent() > page.getPages()) {
            page = productMapper.selectJoinPage(
                    new Page<>(page.getPages(), size),
                    ProductListDTO.class,
                    wrapper);
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

    @Override
    public ApiResponse updateProduct(Long id, ProductUpdateDTO data) {
        Product product = data.toProduct();
        product.setId(id);
        int count = productMapper.updateById(product);
        if (count == 0) {
            return ApiResponse.notFound(String.format("不存在id=%d的商品", id));
        }
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse getProductById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return ApiResponse.notFound(String.format("不存在id=%d的商品", id));
        }
        JSONObject productJson=JSONObject.from(product);
        JSONArray customize=new JSONArray();
        // 查询客制化项目
        QueryWrapper<CustomizeItem> itemWrapper=new QueryWrapper<>();
        itemWrapper.eq("product_id",id);
        itemWrapper.eq("status",1);
        itemWrapper.orderByAsc("sort");
        List<CustomizeItem> customizeItem=customizeItemMapper.selectList(itemWrapper);
        for(CustomizeItem item:customizeItem){
            JSONObject itemJson=JSONObject.from(item);
            // 查询客制化选项
            QueryWrapper<CustomizeOption> optionWrapper=new QueryWrapper<>();
            optionWrapper.eq("item_id",item.getId());
            optionWrapper.eq("status",1);
            optionWrapper.orderByAsc("sort");
            List<CustomizeOption> customizeOption=customizeOptionMapper.selectList(optionWrapper);
            itemJson.put("options",customizeOption);
            customize.add(itemJson);
        }
        productJson.put("customize",customize);
        return ApiResponse.success(JSONObject.of("product", productJson));
    }
}

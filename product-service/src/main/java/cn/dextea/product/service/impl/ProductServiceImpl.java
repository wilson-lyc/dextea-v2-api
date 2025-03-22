package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.OptionDTO;
import cn.dextea.product.dto.*;
import cn.dextea.product.feign.ProductFeign;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.pojo.*;
import cn.dextea.product.service.ProductService;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private ProductFeign productFeign;

    @Override
    public ApiResponse createProduct(ProductCreateDTO data) {
        Product product = data.toProduct();
        product.setGlobalStatus(0);// 默认全局禁售
        // 校验商品分类
        if(!productFeign.isCategoryIdValid(product.getCategoryId())){
            return ApiResponse.badRequest("商品分类不存在");
        }
        // 插入db
        productMapper.insert(product);
        return ApiResponse.success("商品创建成功");
    }

    @Override
    public ApiResponse getProductList(int current, int size, ProductQueryDTO filter) {
        // 构建查询条件
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class,ProductListDTO.class)
                // 商品分类
                .leftJoin(Category.class, Category::getId, Product::getCategoryId)
                .selectFunc("coalesce(%s,\"未知\")",arg ->arg
                                .accept(Category::getName),
                        ProductListDTO::getCategoryName)
                // 搜索条件
                .eqIfExists(Product::getId, filter.getId())
                .likeIfExists(Product::getName, filter.getName())
                .eqIfExists(Product::getCategoryId, filter.getCategoryId())
                .eqIfExists(Product::getGlobalStatus, filter.getStatus())
                .between(filter.getMinPrice()!=null&& filter.getMaxPrice()!=null,Product::getPrice,filter.getMinPrice(),filter.getMaxPrice());
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
    public ApiResponse getProductOption(Integer status) {
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAs(Product::getId, OptionDTO::getValue)
                .selectAs(Product::getName, OptionDTO::getLabel)
                .eqIfExists(Product::getGlobalStatus,status);
        List<OptionDTO> options = productMapper.selectJoinList(OptionDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("count",options.size(),"options", options));
    }

    @Override
    public ApiResponse getProductBase(Long id) {
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class, ProductBaseDTO.class)
                .eq(Product::getId,id);
        ProductBaseDTO product = productMapper.selectJoinOne(ProductBaseDTO.class,wrapper);
        if(product == null) {
            return ApiResponse.notFound(String.format("不存在id=%d的商品", id));
        }
        return ApiResponse.success(JSONObject.of("product", product));
    }

    @Override
    public ApiResponse getProductImg(Long id) {
        JSONArray images=new JSONArray();
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .eq(Product::getId, id)
                .select(Product::getId)
                .select(Product::getCover)
                .select(Product::getDetailHeaderImg);
        Product product=productMapper.selectJoinOne(wrapper);
        if (product == null){
            return ApiResponse.notFound(String.format("不存在id=%d的商品", id));
        }
        // 封面
        images.add(new ProductImgDTO(
                "cover",
                "封面",
                StringUtils.isNotBlank(product.getCover())?product.getCover():null,
                "/product/upload/cover"));
        images.add(new ProductImgDTO(
                "detailHeaderImg",
                "商品详情页头部图",
                StringUtils.isNotBlank(product.getDetailHeaderImg())?product.getDetailHeaderImg():null,
                "/product/upload/detail-header-img"));
        return ApiResponse.success(JSONObject.of("images",images));
    }

    @Override
    public ApiResponse getProductGlobalStatus(Long id) {
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .eq(Product::getId, id)
                .selectAsClass(Product.class,ProductStatusDTO.class);
        ProductStatusDTO productStatus = productMapper.selectJoinOne(ProductStatusDTO.class,wrapper);
        if (productStatus == null) {
            return ApiResponse.notFound(String.format("不存在id=%d的商品", id));
        }
        return ApiResponse.success(JSONObject.of("status", productStatus));
    }

    @Override
    public ApiResponse updateProductBase(Long id, ProductUpdateBaseDTO data) {
        Product product=data.toProduct();
        product.setId(id);
        // 校验分类ID
        if(!productFeign.isCategoryIdValid(product.getCategoryId())){
            return ApiResponse.badRequest("商品分类有误");
        }
        // 更新db
        if (productMapper.updateById(product) == 0) {
            return ApiResponse.notFound("更新失败，可能是不存在该商品");
        }
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse updateProductGlobalStatus(Long id, Integer status) {
        if(status<0 || status>1){
            return ApiResponse.badRequest("状态值有误");
        }
        Product product=Product.builder()
                .id(id)
                .globalStatus(status)
                .build();
        if (productMapper.updateById(product) == 0) {
            return ApiResponse.notFound("更新失败，可能是不存在该商品");
        }
        return ApiResponse.success("更新成功");
    }
}

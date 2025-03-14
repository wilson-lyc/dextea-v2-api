package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.OptionDTO;
import cn.dextea.product.dto.*;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.ProductStatusMapper;
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

    @Override
    public ApiResponse createProduct(ProductCreateDTO data) {
        Product product = data.toProduct();
        product.setGlobalStatus(1);// 默认全局禁售
        productMapper.insert(product);
        return ApiResponse.success("商品创建成功",JSONObject.of("product",product));
    }

    @Override
    public ApiResponse getProductList(int current, int size, ProductQueryDTO filter) {
        // 构建查询条件
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                // 基础
                .selectAsClass(Product.class,ProductListDTO.class)
                // 分类
                .selectAs(Category::getName, ProductListDTO::getCategoryName)
                .innerJoin(Category.class, Category::getId, Product::getCategoryId)
                // 用户搜索条件
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
        return ApiResponse.success(JSONObject.of("options", options,"count",options.size()));
    }

    @Override
    public ApiResponse getProductBase(Long id) {
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class, ProductBaseDTO.class)
                .selectAs(Category::getName, ProductListDTO::getCategoryName)
                .innerJoin(Category.class, Category::getId,Product::getCategoryId)
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
        return ApiResponse.success(JSONObject.of("product", productStatus));
    }

    @Override
    public ApiResponse updateProductBase(Long id, ProductUpdateBaseDTO data) {
        Product product=data.toProduct();
        product.setId(id);
        int count = productMapper.updateById(product);
        if (count == 0) {
            return ApiResponse.notFound(String.format("不存在id=%d的商品", id));
        }
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse updateProductGlobalStatus(Long id, Integer status) {
        Product product=Product.builder()
                .id(id)
                .globalStatus(status)
                .build();
        int count = productMapper.updateById(product);
        if (count == 0) {
            return ApiResponse.notFound(String.format("不存在id=%d的商品", id));
        }
        return ApiResponse.success("更新成功");
    }
}

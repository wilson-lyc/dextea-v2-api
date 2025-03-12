package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.OptionDTO;
import cn.dextea.product.dto.*;
import cn.dextea.product.mapper.CustomizeItemMapper;
import cn.dextea.product.mapper.CustomizeOptionMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.pojo.CustomizeItem;
import cn.dextea.product.pojo.CustomizeOption;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.pojo.Category;
import cn.dextea.product.service.ProductService;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    private CustomizeItemMapper customizeItemMapper;

    @Resource
    private CustomizeOptionMapper customizeOptionMapper;

    @Override
    public ApiResponse createProduct(ProductCreateDTO data) {
        Product product = data.toProduct();
        product.setStatus(0);// 默认禁售
        productMapper.insert(product);
        return ApiResponse.success("商品创建成功");
    }

    @Override
    public ApiResponse getProductList(int current, int size, ProductQueryDTO filter) {
        // 查询条件
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class,ProductListDTO.class)
                .selectAs(Category::getName, ProductListDTO::getCategoryName)
                .innerJoin(Category.class, Category::getId, Product::getCategoryId)
                .eq(filter.getId() != null, Product::getId, filter.getId())
                .like(StringUtils.isNotBlank(filter.getName()), Product::getName, filter.getName())
                .eq(filter.getCategoryId() != null, Product::getCategoryId, filter.getCategoryId())
                .eq(filter.getStatus() != null, Product::getStatus, filter.getStatus())
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
                .selectAs(Product::getName, OptionDTO::getLabel);
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
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
        if(StringUtils.isNotBlank(product.getCover()))
            images.add(new ProductImgDTO("cover", "封面", product.getCover(), "/product/upload/cover"));
        if(StringUtils.isNotBlank(product.getDetailHeaderImg()))
            images.add(new ProductImgDTO("detailHeaderImg", "商品详情页头部图", product.getDetailHeaderImg(), "/product/upload/detail-header-img"));
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
    public ApiResponse updateProduct(Long id, Product product) {
        product.setId(id);
        int count = productMapper.updateById(product);
        if (count == 0) {
            return ApiResponse.notFound(String.format("不存在id=%d的商品", id));
        }
        return ApiResponse.success("更新成功");
    }
}

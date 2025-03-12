package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.*;
import cn.dextea.product.mapper.CustomizeItemMapper;
import cn.dextea.product.mapper.CustomizeOptionMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.pojo.CustomizeItem;
import cn.dextea.product.pojo.CustomizeOption;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.pojo.ProductCategory;
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
        productMapper.insert(product);
        return ApiResponse.success("创建成功");
    }

    @Override
    public ApiResponse getProductList(int current, int size, ProductQueryDTO filter) {
        // 查询条件
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class,ProductListDTO.class)
                .selectAs(ProductCategory::getName, ProductListDTO::getCategoryName)
                .innerJoin(ProductCategory.class, ProductCategory::getId, Product::getCategoryId)
                .orderByAsc(Product::getId)
                .eq(filter.getId() != null, Product::getId, filter.getId())
                .like(StringUtils.isNotBlank(filter.getName()), Product::getName, filter.getName())
                .eq(filter.getCategoryId() != null, Product::getCategoryId, filter.getCategoryId())
                .eq(filter.getStatus() != null, Product::getStatus, filter.getStatus())
                .between(filter.getMinPrice()!=null&& filter.getMaxPrice()!=null,Product::getPrice,filter.getMinPrice(),filter.getMaxPrice());
        // 门店
        if(filter.getStoreId()!=null){
            //TODO: 注入门店相关的限制条件
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
    public ApiResponse getProductBaseById(Long id) {
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class, ProductBaseDTO.class)
                .selectAs(ProductCategory::getName, ProductListDTO::getCategoryName)
                .innerJoin(ProductCategory.class,ProductCategory::getId,Product::getCategoryId)
                .eq(Product::getId,id);
        ProductBaseDTO product = productMapper.selectJoinOne(ProductBaseDTO.class,wrapper);
        if(product == null) {
            return ApiResponse.notFound(String.format("不存在id=%d的商品", id));
        }
        return ApiResponse.success(JSONObject.of("product", product));
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
    public ApiResponse getProductImgById(Long id) {
        JSONArray images=new JSONArray();
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .eq(Product::getId, id)
                .select(Product::getId)
                .select(Product::getCover);
        Product product=productMapper.selectJoinOne(wrapper);
        // 封面
        images.add(new ProductImgDTO("cover", "封面", product.getCover(), "/product/cover"));
        return ApiResponse.success(JSONObject.of("images",images));
    }

    @Override
    public ApiResponse updateProductBase(Long id, ProductUpdateDTO data) {
        Product product = data.toProduct();
        product.setId(id);
        int count = productMapper.updateById(product);
        if (count == 0) {
            return ApiResponse.notFound(String.format("不存在id=%d的商品", id));
        }
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse getProductForCustomer(Long id,Long storeId) {
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

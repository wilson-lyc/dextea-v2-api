package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.OptionDTO;
import cn.dextea.product.dto.product.*;
import cn.dextea.product.feign.ProductFeign;
import cn.dextea.product.feign.StoreFeign;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.ProductStatusMapper;
import cn.dextea.product.pojo.*;
import cn.dextea.product.service.ProductService;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductStatusMapper productStatusMapper;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private StoreFeign storeFeign;

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
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class, ProductListDTO.class)
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
    public ApiResponse getProductList(Long storeId, int current, int size, ProductQueryDTO filter) {
        // 预处理全局状态和门店状态
        Integer globalStatus=null;
        Integer storeStatus=null;
        if (Objects.nonNull(filter.getStatus())){
            if(filter.getStatus()==0){
                globalStatus = 0;
            }else{
                globalStatus = 1;
                storeStatus = filter.getStatus();
            }
        }
        // 构建查询条件
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class, ProductListDTO.class)
                // 商品分类
                .leftJoin(Category.class, Category::getId, Product::getCategoryId)
                .selectFunc("coalesce(%s,\"未知\")",arg ->arg
                                .accept(Category::getName),
                        ProductListDTO::getCategoryName)
                // 门店状态 - 表内无记录说明禁售
                .leftJoin(ProductStoreStatus.class,"ps", on -> on
                        .eq(ProductStoreStatus::getProductId,Product::getId)
                        .eq(ProductStoreStatus::getStoreId,storeId))
                .selectFunc("coalesce(%s,3)",arg ->arg
                                .accept(ProductStoreStatus::getStatus),
                        ProductListDTO::getStoreStatus)
                // 门店状态的自定义查询
                .isNull(Objects.nonNull(storeStatus) && storeStatus==3,"ps", ProductStoreStatus::getStatus)
                .eq(Objects.nonNull(storeStatus) && storeStatus!=3,"ps", ProductStoreStatus::getStatus,storeStatus)
                // 用户搜索条件
                .eqIfExists(Product::getId, filter.getId())
                .likeIfExists(Product::getName, filter.getName())
                .eqIfExists(Product::getCategoryId, filter.getCategoryId())
                .eqIfExists(Product::getGlobalStatus, globalStatus)// 全局状态的自定义条件
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
    public ApiResponse getProductStatus(Long productId) {
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .eq(Product::getId, productId)
                .selectAsClass(Product.class, ProductStatusDTO.class);
        ProductStatusDTO status = productMapper.selectJoinOne(ProductStatusDTO.class,wrapper);
        if (status == null) {
            return ApiResponse.notFound(String.format("不存在id=%d的商品", productId));
        }
        return ApiResponse.success(JSONObject.of("status", status));
    }

    @Override
    public ApiResponse getProductStatus(Long productId, Long storeId) {
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                // 全局状态
                .selectAs(Product::getGlobalStatus, ProductStatusDTO::getGlobalStatus)
                // 门店状态
                .leftJoin(ProductStoreStatus.class,"ps", on -> on
                        .eq(ProductStoreStatus::getProductId,Product::getId)
                        .eq(ProductStoreStatus::getStoreId,storeId))
                .selectFunc("coalesce(%s,3)",arg ->arg
                                .accept(ProductStoreStatus::getStatus),
                        ProductStatusDTO::getStoreStatus)
                .eq(Product::getId,productId);
        ProductStatusDTO status=productMapper.selectJoinOne(ProductStatusDTO.class,wrapper);
        if (Objects.isNull(status)){
            return ApiResponse.notFound("请求参数有误");
        }
        return ApiResponse.success(JSONObject.of("status",status));
    }

    @Override
    public ApiResponse updateProductBase(Long id, ProductUpdateBaseDTO data) {
        Product product=data.toProduct();
        product.setId(id);
        // 校验分类
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
    public ApiResponse updateProductStatus(Long productId, Integer status) {
        //校验状态码
        if(status<1 || status>3){
            return ApiResponse.badRequest("状态值有误");
        }
        // 更新db
        Product product=Product.builder()
                .id(productId)
                .globalStatus(status)
                .build();
        if (productMapper.updateById(product) == 0) {
            return ApiResponse.notFound("更新失败，可能是不存在该商品");
        }
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse updateProductStatus(Long productId, Long storeId, Integer status) {
        //校验状态码
        if(status<1 || status>3){
            return ApiResponse.badRequest("状态值有误");
        }
        // 更新db
        MPJLambdaWrapper<ProductStoreStatus> wrapper = new MPJLambdaWrapper<ProductStoreStatus>()
                .eq(ProductStoreStatus::getStoreId,storeId)
                .eq(ProductStoreStatus::getProductId,productId);
        ProductStoreStatus productStoreStatus =productStatusMapper.selectOne(wrapper);
        if(status==3){
            if(Objects.nonNull(productStoreStatus)){
                productStatusMapper.delete(wrapper);
            }
        }else{
            if(Objects.isNull(productStoreStatus)){
                productStoreStatus = ProductStoreStatus.builder()
                        .storeId(storeId)
                        .productId(productId)
                        .status(status)
                        .build();
                productStatusMapper.insert(productStoreStatus);
            }else {
                UpdateWrapper<ProductStoreStatus> updateWrapper=new UpdateWrapper<>();
                updateWrapper.set("status",status);
                updateWrapper.eq("store_id",storeId);
                updateWrapper.eq("product_id",productId);
               productStatusMapper.update(updateWrapper);
            }
        }
        return ApiResponse.success("更新成功");
    }
}

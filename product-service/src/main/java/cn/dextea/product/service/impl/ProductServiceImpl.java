package cn.dextea.product.service.impl;

import cn.dextea.common.code.ProductStatus;
import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.OptionDTO;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.feign.StoreFeign;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.pojo.ProductCategory;
import cn.dextea.product.pojo.ProductStoreStatus;
import cn.dextea.product.dto.product.*;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.ProductStatusMapper;
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
import org.apache.ibatis.javassist.NotFoundException;
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
        // 校验商品分类
        if(!productFeign.isCategoryIdValid(data.getCategoryId())){
            throw new IllegalArgumentException("categoryId错误");
        }
        // 创建商品
        Product product = data.toProduct();
        productMapper.insert(product);
        return ApiResponse.success("商品创建成功");
    }

    /**
     * 分页查询商品列表
     * @param current 当前页码
     * @param size 分页大小
     * @param wrapper 查询条件
     */
    private IPage<ProductListDTO> getProductListPage(int current, int size,MPJLambdaWrapper<Product> wrapper){
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
        return page;
    }

    /**
     * 获取商品列表 - 只返回全局状态
     * @param current 当前页码
     * @param size 分页大小
     * @param filter 搜索条件
     */
    @Override
    public ApiResponse getProductList(int current, int size, ProductQueryDTO filter) {
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class, ProductListDTO.class)
                // 商品分类
                .leftJoin(ProductCategory.class, ProductCategory::getId, Product::getCategoryId)
                .selectFunc("coalesce(%s,\"未知\")",arg ->arg
                                .accept(ProductCategory::getName),
                        ProductListDTO::getCategoryName)
                // 搜索条件
                .eqIfExists(Product::getId, filter.getId())
                .likeIfExists(Product::getName, filter.getName())
                .eqIfExists(Product::getCategoryId, filter.getCategoryId())
                .eqIfExists(Product::getGlobalStatus, filter.getStatus())
                .between(filter.getMinPrice()!=null&& filter.getMaxPrice()!=null,Product::getPrice,filter.getMinPrice(),filter.getMaxPrice());
        // 分页查询
        return ApiResponse.success(JSONObject.from(getProductListPage(current,size,wrapper)));
    }

    /**
     * 查询商品列表 - 返回全局状态和门店状态
     * @param storeId 门店ID
     * @param current 当前页码
     * @param size 分页大小
     * @param filter 搜索条件
     */
    @Override
    public ApiResponse getProductList(Long storeId, int current, int size, ProductQueryDTO filter){
        // 校验门店ID
        if(!storeFeign.isStoreIdValid(storeId))
            throw new IllegalArgumentException("storeId错误");
        // 构建查询条件
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class, ProductListDTO.class)
                // 商品分类
                .leftJoin(ProductCategory.class, ProductCategory::getId, Product::getCategoryId)
                .selectFunc("coalesce(%s,\"未知\")",arg ->arg
                                .accept(ProductCategory::getName),
                        ProductListDTO::getCategoryName)
                // 门店状态 - 无记录代表禁售
                .leftJoin(ProductStoreStatus.class,"ps", on -> on
                        .eq(ProductStoreStatus::getProductId,Product::getId)
                        .eq(ProductStoreStatus::getStoreId,storeId))
                .selectFunc("coalesce(%s,3)",arg ->arg
                                .accept(ProductStoreStatus::getStatus),
                        ProductListDTO::getStoreStatus)
                // 搜索条件
                // 门店状态的自定义查询
                .isNull(Objects.nonNull(filter.getStoreStatus()) && filter.getStoreStatus()==3,"ps", ProductStoreStatus::getStatus)
                .eq(Objects.nonNull(filter.getStoreStatus()) && filter.getStoreStatus()!=3,"ps", ProductStoreStatus::getStatus,filter.getStoreStatus())
                // 一般搜索条件
                .eqIfExists(Product::getId, filter.getId())
                .likeIfExists(Product::getName, filter.getName())
                .eqIfExists(Product::getCategoryId, filter.getCategoryId())
                .eqIfExists(Product::getGlobalStatus, filter.getGlobalStatus())
                .between(filter.getMinPrice()!=null&& filter.getMaxPrice()!=null,Product::getPrice,filter.getMinPrice(),filter.getMaxPrice());
        // 分页查询
        return ApiResponse.success(JSONObject.from(getProductListPage(current,size,wrapper)));
    }

    @Override
    public ApiResponse getProductOption(Integer globalStatus) {
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAs(Product::getId, OptionDTO::getValue)
                .selectAs(Product::getName, OptionDTO::getLabel)
                .eqIfExists(Product::getGlobalStatus,globalStatus);
        List<OptionDTO> options = productMapper.selectJoinList(OptionDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("count",options.size(),"options", options));
    }

    @Override
    public ApiResponse getProductBase(Long id) throws NotFoundException {
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class, ProductBaseDTO.class)
                .leftJoin(ProductCategory.class,ProductCategory::getId,Product::getCategoryId)
                .selectAs(ProductCategory::getName,ProductBaseDTO::getCategoryName)
                .eq(Product::getId,id);
        ProductBaseDTO product = productMapper.selectJoinOne(ProductBaseDTO.class,wrapper);
        if(Objects.isNull(product)) {
            throw new NotFoundException("商品不存在");
        }
        return ApiResponse.success(JSONObject.of("product", product));
    }

    @Override
    public ApiResponse getProductImg(Long id) throws NotFoundException {
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .eq(Product::getId, id)
                .select(Product::getId)
                .select(Product::getCover)
                .select(Product::getDetailHeaderImg);
        Product product=productMapper.selectJoinOne(wrapper);
        if(Objects.isNull(product)) {
            throw new NotFoundException("商品不存在");
        }
        JSONArray images=new JSONArray();
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

    /**
     * 获取商品状态 - 只有全局状态
     * @param productId 商品ID
     */
    @Override
    public ApiResponse getProductStatus(Long productId) throws NotFoundException {
        Integer globalStatus = productFeign.getProductGlobalStatus(productId);
        if (Objects.isNull(globalStatus)) {
            throw new NotFoundException("商品不存在");
        }
        ProductStatusDTO status=new ProductStatusDTO(globalStatus);
        return ApiResponse.success(JSONObject.of("status", status));
    }

    /**
     * 获取商品状态 - 全局状态+门店状态
     * @param productId 商品ID
     * @param storeId 门店ID
     */
    @Override
    public ApiResponse getProductStatus(Long productId, Long storeId) throws NotFoundException {
        // 校验门店ID
        if(!storeFeign.isStoreIdValid(storeId))
            throw new IllegalArgumentException("storeId错误");
        // 全局状态
        Integer globalStatus = productFeign.getProductGlobalStatus(productId);
        if (Objects.isNull(globalStatus))
            throw new NotFoundException("商品不存在");
        // 门店状态
        Integer storeStatus=productFeign.getProductStoreStatus(productId,storeId);
        // 构建结果
        ProductStatusDTO status=new ProductStatusDTO(globalStatus,storeStatus);
        return ApiResponse.success(JSONObject.of("status", status));
    }

    @Override
    public ApiResponse updateProductBase(Long id, ProductUpdateBaseDTO data) throws NotFoundException {
        // 校验分类
        if(!productFeign.isCategoryIdValid(data.getCategoryId()))
            throw new IllegalArgumentException("categoryId错误");
        // 更新数据
        Product product=data.toProduct(id);
        if (productMapper.updateById(product) == 0) {
            throw new NotFoundException("商品不存在");
        }
        return ApiResponse.success("更新成功");
    }

    /**
     * 更新全局状态
     * @param productId 商品ID
     * @param status 状态
     */
    @Override
    public ApiResponse updateProductStatus(Long productId, Integer status) throws NotFoundException {
        // 校验状态码
        if(status!=ProductStatus.GLOBAL_FORBIDDEN.getValue() && status!=ProductStatus.AVAILABLE.getValue())
            throw new IllegalArgumentException("商品状态码错误");
        // 更新db
        Product product=Product.builder()
                .id(productId)
                .globalStatus(status)
                .build();
        if (productMapper.updateById(product) == 0)
            throw new NotFoundException("商品不存在");
        return ApiResponse.success("更新成功");
    }

    /**
     * 修改门店状态
     * @param productId 商品ID
     * @param storeId 门店ID
     * @param status 状态
     */
    @Override
    public ApiResponse updateProductStatus(Long productId, Long storeId, Integer status) throws NotFoundException {
        // 校验门店ID
        if(!storeFeign.isStoreIdValid(storeId))
            throw new IllegalArgumentException("storeId错误");
        //校验状态码
        if(status==ProductStatus.GLOBAL_FORBIDDEN.getValue())
            throw new IllegalArgumentException("商品状态码错误");
        // 更新db
        MPJLambdaWrapper<ProductStoreStatus> wrapper = new MPJLambdaWrapper<ProductStoreStatus>()
                .eq(ProductStoreStatus::getStoreId,storeId)
                .eq(ProductStoreStatus::getProductId,productId);
        ProductStoreStatus productStoreStatus =productStatusMapper.selectJoinOne(wrapper);
        if(status==ProductStatus.STORE_FORBIDDEN.getValue()){
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

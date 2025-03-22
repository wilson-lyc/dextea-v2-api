package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.product.ProductListDTO;
import cn.dextea.product.dto.product.ProductQueryDTO;
import cn.dextea.product.dto.product.ProductStatusDTO;
import cn.dextea.product.feign.ProductFeign;
import cn.dextea.product.feign.StoreFeign;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.ProductStatusMapper;
import cn.dextea.product.pojo.Category;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.pojo.ProductStatus;
import cn.dextea.product.service.StoreService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class StoreServiceImpl implements StoreService {
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductStatusMapper productStatusMapper;
    @Resource
    private StoreFeign storeFeign;
    @Resource
    private ProductFeign productFeign;
    @Override
    public ApiResponse getStoreProductList(Long storeId, Integer current, Integer size, ProductQueryDTO filter) {
        // 预处理全局状态和门店状态
        Integer globalStatus = null;
        Integer storeStatus = null;
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
                .leftJoin(ProductStatus.class,"ps", on -> on
                        .eq(ProductStatus::getProductId,Product::getId)
                        .eq(ProductStatus::getStoreId,storeId))
                .selectFunc("coalesce(%s,3)",arg ->arg
                                .accept(ProductStatus::getStatus),
                        ProductListDTO::getStoreStatus)
                // 门店状态的自定义查询
                .isNull(Objects.nonNull(storeStatus) && storeStatus==3,"ps",ProductStatus::getStatus)
                .eq(Objects.nonNull(storeStatus) && storeStatus!=3,"ps",ProductStatus::getStatus,storeStatus)
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
    public ApiResponse getProductStoreStatus(Long storeId, Long productId) {
        // 构建查询条件
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                // 全局状态
                .selectAs(Product::getGlobalStatus, ProductStatusDTO::getGlobalStatus)
                // 门店状态
                .leftJoin(ProductStatus.class,"ps", on -> on
                        .eq(ProductStatus::getProductId,Product::getId)
                        .eq(ProductStatus::getStoreId,storeId))
                .selectFunc("coalesce(%s,3)",arg ->arg
                                .accept(ProductStatus::getStatus),
                        ProductStatusDTO::getStoreStatus)
                .eq(Product::getId,productId);
        ProductStatusDTO status=productMapper.selectJoinOne(ProductStatusDTO.class,wrapper);
        if(Objects.isNull(status)){
            return ApiResponse.notFound(String.format("不存在id=%d的商品", productId));
        }
        return ApiResponse.success(JSONObject.of("status",status));
    }

    @Override
    public ApiResponse updateProductStoreStatus(Long storeId, Long productId, Integer status) {
        if(status<1||status>3||!storeFeign.isStoreIdValid(storeId)||!productFeign.isProductIdValid(productId)){
            return ApiResponse.badRequest("请求参数错误");
        }
        MPJLambdaWrapper<ProductStatus> wrapper = new MPJLambdaWrapper<ProductStatus>()
                .eq(ProductStatus::getStoreId,storeId)
                .eq(ProductStatus::getProductId,productId);
        ProductStatus productStatus=productStatusMapper.selectOne(wrapper);
        if(status==3){
            if(Objects.nonNull(productStatus)){
                productStatusMapper.delete(wrapper);
            }
        }else {
            if(Objects.isNull(productStatus)){
                productStatus=ProductStatus.builder()
                        .storeId(storeId)
                        .productId(productId)
                        .status(status)
                        .build();
                productStatusMapper.insert(productStatus);
            }else {
                UpdateWrapper<ProductStatus> updateWrapper=new UpdateWrapper<>();
                updateWrapper.set("status",status);
                updateWrapper.eq("store_id",storeId);
                updateWrapper.eq("product_id",productId);
                productStatusMapper.update(updateWrapper);
            }
        }
        return ApiResponse.success("更新成功");
    }
}

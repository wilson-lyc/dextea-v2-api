package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.ProductListDTO;
import cn.dextea.product.dto.ProductQueryDTO;
import cn.dextea.product.feign.StoreFeign;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.ProductStatusMapper;
import cn.dextea.product.pojo.Category;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.pojo.ProductStatus;
import cn.dextea.product.service.StoreService;
import com.alibaba.fastjson2.JSONObject;
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
    @Override
    public ApiResponse getStoreProductList(Long storeId, Integer current, Integer size, ProductQueryDTO filter) {
        // 预处理status的查询条件，拆分成全局和门店的
        Integer globalStatus = null;
        Integer storeStatus = null;
        if (Objects.nonNull(filter.getStatus())){
            switch (filter.getStatus()) {
                case 0:
                    globalStatus = 0;
                    storeStatus = 0;
                    break;
                case 1:
                    globalStatus = 0;
                    storeStatus = 1;
                    break;
                case 2:
                    globalStatus = 0;
                    storeStatus = 2;
                    break;
                case 3:
                    globalStatus = 1;
                    break;
            }
        }
        // 构建查询条件
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                // 基础
                .selectAsClass(Product.class, ProductListDTO.class)
                // 分类
                .selectAs(Category::getName, ProductListDTO::getCategoryName)
                .innerJoin(Category.class, Category::getId, Product::getCategoryId)
                // 门店状态
                .leftJoin(ProductStatus.class,"ps", on -> on
                        .eq(ProductStatus::getProductId,Product::getId)
                        .eq(ProductStatus::getStoreId,storeId))
                .selectFunc("coalesce(%s,0)",arg ->arg
                                .accept(ProductStatus::getStatus),
                        ProductListDTO::getStoreStatus)
                // 门店状态的自定义查询
                .isNull(Objects.nonNull(storeStatus) && storeStatus==0,"ps",ProductStatus::getStatus)
                .eq(Objects.nonNull(storeStatus) && storeStatus!=0,"ps",ProductStatus::getStatus,storeStatus)
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
        // 判断storeId和productId是否存在
        if (!storeFeign.isStoreIdValid(storeId)){
            return ApiResponse.notFound("storeId不存在");
        }
        if (productMapper.selectById(productId)==null){
            return ApiResponse.notFound("productId不存在");
        }
        // 构建查询条件
        MPJLambdaWrapper<ProductStatus> wrapper = new MPJLambdaWrapper<ProductStatus>()
                .eq(ProductStatus::getStoreId,storeId)
                .eq(ProductStatus::getProductId,productId);
        ProductStatus productStatus=productStatusMapper.selectOne(wrapper);
        if (Objects.isNull(productStatus)){
            return ApiResponse.success(JSONObject.of("status",0));
        }
        return ApiResponse.success(JSONObject.of("status",productStatus.getStatus()));
    }

    @Override
    public ApiResponse updateProductStoreStatus(Long storeId, Long productId, Integer status) {
        MPJLambdaWrapper<ProductStatus> wrapper = new MPJLambdaWrapper<ProductStatus>()
                .eq(ProductStatus::getStoreId,storeId)
                .eq(ProductStatus::getProductId,productId);
        ProductStatus productStatus=productStatusMapper.selectOne(wrapper);
        if(Objects.isNull(productStatus)){
            if (status!=0){
                ProductStatus temp=ProductStatus.builder()
                        .storeId(storeId)
                        .productId(productId)
                        .status(status)
                        .build();
                productStatusMapper.insert(temp);
                return ApiResponse.success("更新成功");
            }else{
                return ApiResponse.success("更新成功");
            }
        }else{
            if(status==0){
                productStatusMapper.delete(wrapper);
                return ApiResponse.success("更新成功");
            }else{
                productStatus.setStatus(status);
                productStatusMapper.update(productStatus,wrapper);
                return ApiResponse.success("更新成功");
            }
        }
    }
}

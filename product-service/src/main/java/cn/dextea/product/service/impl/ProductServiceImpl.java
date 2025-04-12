package cn.dextea.product.service.impl;

import cn.dextea.common.code.ProductStatus;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.common.ImageModel;
import cn.dextea.common.model.common.SelectOptionModel;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.feign.StoreFeign;
import cn.dextea.common.model.product.ProductModel;
import cn.dextea.product.code.ProductErrorCode;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.pojo.ProductCategory;
import cn.dextea.product.pojo.ProductStoreStatus;
import cn.dextea.product.model.product.*;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.ProductStatusMapper;
import cn.dextea.product.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.dvcs.VPKCRequestBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public DexteaApiResponse<Void> createProduct(ProductCreateRequest data) {
        // 校验商品分类
        if(!productFeign.isCategoryIdValid(data.getCategoryId())){
            return DexteaApiResponse.fail(ProductErrorCode.CATEGORY_ID_ERROR.getCode(),
                    ProductErrorCode.CATEGORY_ID_ERROR.getMsg());
        }
        // 创建商品
        Product product = data.toProduct();
        productMapper.insert(product);
        return DexteaApiResponse.success();
    }

    // 执行分页查询
    private IPage<ProductModel> getProductListPage(int current, int size,MPJLambdaWrapper<Product> wrapper){
        IPage<ProductModel> page=productMapper.selectJoinPage(
                new Page<>(current, size),
                ProductModel.class,
                wrapper);
        if (page.getCurrent() > page.getPages()) {
            page = productMapper.selectJoinPage(
                    new Page<>(page.getPages(), size),
                    ProductModel.class,
                    wrapper);
        }
        return page;
    }

    // 获取商品列表 - 只返回全局状态
    @Override
    public DexteaApiResponse<IPage<ProductModel>> getProductList(int current, int size, ProductFilter filter) {
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class, ProductModel.class)
                // 商品分类
                .leftJoin(ProductCategory.class, ProductCategory::getId, Product::getCategoryId)
                .selectFunc("coalesce(%s,\"未知\")",arg ->arg
                                .accept(ProductCategory::getName),
                        ProductModel::getCategoryText)
                // 搜索条件
                .eqIfExists(Product::getId, filter.getId())
                .likeIfExists(Product::getName, filter.getName())
                .eqIfExists(Product::getCategoryId, filter.getCategoryId())
                .eqIfExists(Product::getGlobalStatus, filter.getStatus())
                .geIfExists(Product::getPrice,filter.getMinPrice())
                .leIfExists(Product::getPrice,filter.getMaxPrice());
        // 分页查询
        return DexteaApiResponse.success(getProductListPage(current,size,wrapper));
    }

    // 查询商品列表 - 返回全局状态和门店状态
    @Override
    public DexteaApiResponse<IPage<ProductModel>> getProductList(Long storeId, int current, int size, ProductFilter filter){
        // 校验门店ID
        if(!storeFeign.isStoreIdValid(storeId)){
            return DexteaApiResponse.fail(ProductErrorCode.STORE_ID_ERROR.getCode(),
                    ProductErrorCode.STORE_ID_ERROR.getMsg());
        }
        // 构建查询条件
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class, ProductModel.class)
                // 商品分类
                .leftJoin(ProductCategory.class, ProductCategory::getId, Product::getCategoryId)
                .selectFunc("coalesce(%s,\"未知\")",arg ->arg
                                .accept(ProductCategory::getName),
                        ProductModel::getCategoryText)
                // 门店状态 - 无记录代表禁售
                .leftJoin(ProductStoreStatus.class,"ps", on -> on
                        .eq(ProductStoreStatus::getProductId,Product::getId)
                        .eq(ProductStoreStatus::getStoreId,storeId))
                .selectFunc("coalesce(%s,3)",arg ->arg
                                .accept(ProductStoreStatus::getStatus),
                        ProductModel::getStoreStatus)
                // 搜索条件
                // 门店状态的自定义查询
                .isNull(Objects.nonNull(filter.getStoreStatus()) && filter.getStoreStatus()==3,"ps", ProductStoreStatus::getStatus)
                .eq(Objects.nonNull(filter.getStoreStatus()) && filter.getStoreStatus()!=3,"ps", ProductStoreStatus::getStatus,filter.getStoreStatus())
                // 一般搜索条件
                .eqIfExists(Product::getId, filter.getId())
                .likeIfExists(Product::getName, filter.getName())
                .eqIfExists(Product::getCategoryId, filter.getCategoryId())
                .eqIfExists(Product::getGlobalStatus, filter.getGlobalStatus())
                .geIfExists(Product::getPrice,filter.getMinPrice())
                .leIfExists(Product::getPrice,filter.getMaxPrice());
        // 分页查询
        return DexteaApiResponse.success(getProductListPage(current,size,wrapper));
    }

    @Override
    public DexteaApiResponse<List<SelectOptionModel>> getProductOption() {
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAs(Product::getId, SelectOptionModel::getValue)
                .selectAs(Product::getName, SelectOptionModel::getLabel);
        List<SelectOptionModel> options = productMapper.selectJoinList(SelectOptionModel.class,wrapper);
        return DexteaApiResponse.success(options);
    }

    @Override
    public DexteaApiResponse<ProductModel> getProductBase(Long id){
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class, ProductModel.class)
                .leftJoin(ProductCategory.class,ProductCategory::getId,Product::getCategoryId)
                .selectAs(ProductCategory::getName,ProductModel::getCategoryText)
                .eq(Product::getId,id);
        ProductModel product = productMapper.selectJoinOne(ProductModel.class,wrapper);
        if(Objects.isNull(product)) {
            return DexteaApiResponse.notFound(ProductErrorCode.PRODUCT_NOT_FOUND.getCode(),
                    ProductErrorCode.PRODUCT_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success(product);
    }

    @Override
    public DexteaApiResponse<List<ImageModel>> getProductImg(Long id){
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .eq(Product::getId, id)
                .select(Product::getId)
                .select(Product::getCover)
                .select(Product::getDetailHeaderImg);
        Product product=productMapper.selectJoinOne(wrapper);
        if(Objects.isNull(product)) {
            return DexteaApiResponse.notFound(ProductErrorCode.PRODUCT_NOT_FOUND.getCode(),
                    ProductErrorCode.PRODUCT_NOT_FOUND.getMsg());
        }
        List<ImageModel>list=new ArrayList<>();
        // 封面
        ImageModel cover= ImageModel.builder()
                .key("cover")
                .name("封面")
                .action("/product/upload/cover")
                .url(product.getCover())
                .build();
        list.add(cover);
        // 详情页头图
        ImageModel header= ImageModel.builder()
                .key("detailHeaderImg")
                .name("详情页头图")
                .action("/product/upload/detail-header-img")
                .url(product.getDetailHeaderImg())
                .build();
        list.add(header);
        return DexteaApiResponse.success(list);
    }

    // 获取商品状态 - 只有全局状态
    @Override
    public DexteaApiResponse<ProductModel> getProductStatus(Long productId){
        Integer globalStatus = productFeign.getProductGlobalStatus(productId);
        if(Objects.isNull(globalStatus)) {
            return DexteaApiResponse.notFound(ProductErrorCode.PRODUCT_NOT_FOUND.getCode(),
                    ProductErrorCode.PRODUCT_NOT_FOUND.getMsg());
        }
        ProductModel product=ProductModel.builder()
                .globalStatus(globalStatus)
                .build();
        return DexteaApiResponse.success(product);
    }

    // 获取商品状态 - 全局状态+门店状态
    @Override
    public DexteaApiResponse<ProductModel> getProductStatus(Long productId, Long storeId){
        // 校验门店ID
        if(!storeFeign.isStoreIdValid(storeId)){
            return DexteaApiResponse.fail(ProductErrorCode.STORE_ID_ERROR.getCode(),
                    ProductErrorCode.STORE_ID_ERROR.getMsg());
        }
        // 全局状态
        Integer globalStatus = productFeign.getProductGlobalStatus(productId);
        if(Objects.isNull(globalStatus)) {
            return DexteaApiResponse.notFound(ProductErrorCode.PRODUCT_NOT_FOUND.getCode(),
                    ProductErrorCode.PRODUCT_NOT_FOUND.getMsg());
        }
        // 门店状态
        Integer storeStatus=productFeign.getProductStoreStatus(productId,storeId);
        // 构建结果
        ProductModel product=ProductModel.builder()
                .globalStatus(globalStatus)
                .storeStatus(storeStatus)
                .build();
        return DexteaApiResponse.success(product);
    }

    @Override
    public DexteaApiResponse<Void> updateProductBase(Long id, ProductUpdateBaseRequest data){
        // 校验分类
        if(!productFeign.isCategoryIdValid(data.getCategoryId())) {
            return DexteaApiResponse.fail(ProductErrorCode.CATEGORY_ID_ERROR.getCode(),
                    ProductErrorCode.CATEGORY_ID_ERROR.getMsg());
        }
        // 更新数据
        Product product=data.toProduct(id);
        if (productMapper.updateById(product) == 0) {
            return DexteaApiResponse.notFound(ProductErrorCode.PRODUCT_NOT_FOUND.getCode(),
                    ProductErrorCode.PRODUCT_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success();
    }

    // 更新全局状态
    @Override
    public DexteaApiResponse<VPKCRequestBuilder> updateProductStatus(Long productId, Integer status){
        // 校验状态码 - 全局只有1和0
        if(status!=ProductStatus.GLOBAL_FORBIDDEN.getValue() && status!=ProductStatus.AVAILABLE.getValue()){
            return DexteaApiResponse.fail(ProductErrorCode.GLOBAL_STATUS_ERROR.getCode(),
                    ProductErrorCode.GLOBAL_STATUS_ERROR.getMsg());
        }
        // 更新db
        LambdaUpdateWrapper<Product> wrapper=new LambdaUpdateWrapper<Product>()
                .eq(Product::getId,productId)
                .set(Product::getGlobalStatus,status);
        if (productMapper.update(wrapper) == 0) {
            return DexteaApiResponse.notFound(ProductErrorCode.PRODUCT_NOT_FOUND.getCode(),
                    ProductErrorCode.PRODUCT_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success();
    }

    // 更新门店状态
    @Override
    public DexteaApiResponse<VPKCRequestBuilder> updateProductStatus(Long productId, Long storeId, Integer status){
        // 校验门店ID
        if(!storeFeign.isStoreIdValid(storeId)){
            return DexteaApiResponse.fail(ProductErrorCode.STORE_ID_ERROR.getCode(),
                    ProductErrorCode.STORE_ID_ERROR.getMsg());
        }
        //校验状态码
        if(status==ProductStatus.GLOBAL_FORBIDDEN.getValue()){
            return DexteaApiResponse.fail(ProductErrorCode.STORE_STATUS_ERROR.getCode(),
                    ProductErrorCode.STORE_STATUS_ERROR.getMsg());
        }
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
                LambdaUpdateWrapper<ProductStoreStatus> updateWrapper=new LambdaUpdateWrapper<>();
                updateWrapper.set(ProductStoreStatus::getStatus,status);
                updateWrapper.eq(ProductStoreStatus::getStoreId,storeId);
                updateWrapper.eq(ProductStoreStatus::getProductId,productId);
               productStatusMapper.update(updateWrapper);
            }
        }
        return DexteaApiResponse.success();
    }
}

package cn.dextea.product.service.impl;

import cn.dextea.common.code.CustomizeItemStatus;
import cn.dextea.common.code.CustomizeOptionStatus;
import cn.dextea.common.code.ProductStatus;
import cn.dextea.common.model.product.CustomizeItemModel;
import cn.dextea.common.model.product.CustomizeOptionModel;
import cn.dextea.common.model.product.ProductModel;
import cn.dextea.product.mapper.*;
import cn.dextea.product.pojo.*;
import cn.dextea.product.service.InternalService;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class InternalServiceImpl implements InternalService {
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductStatusMapper productStatusMapper;
    @Resource
    private ItemMapper itemMapper;
    @Resource
    private OptionMapper optionMapper;
    @Resource
    private OptionStatusMapper optionStatusMapper;

    @Override
    public boolean isProductIdValid(Long id) {
        return Objects.nonNull(productMapper.selectById(id));
    }

    @Override
    public boolean isCategoryIdValid(Long id) {
        return Objects.nonNull(categoryMapper.selectById(id));
    }

    @Override
    public boolean isCustomizeItemIdValid(Long id) {
        return Objects.nonNull(itemMapper.selectById(id));
    }

    @Override
    public boolean isCustomizeOptionIdValid(Long id) {
        return Objects.nonNull(optionMapper.selectById(id));
    }

    // 获取商品状态
    @Override
    public Integer getProductGlobalStatus(Long productId) {
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .select(Product::getGlobalStatus)
                .eq(Product::getId, productId);
        return productMapper.selectJoinOne(Integer.class,wrapper);
    }

    @Override
    public Integer getProductStoreStatus(Long productId, Long storeId) {
        MPJLambdaWrapper<ProductStoreStatus> wrapper = new MPJLambdaWrapper<ProductStoreStatus>()
                .eq(ProductStoreStatus::getProductId,productId)
                .eq(ProductStoreStatus::getStoreId,storeId)
                .select(ProductStoreStatus::getStatus);
        Integer storeStatus=productStatusMapper.selectJoinOne(Integer.class,wrapper);
        // 商品门店状态默认禁售
        return Objects.isNull(storeStatus)?ProductStatus.STORE_FORBIDDEN.getValue():storeStatus;
    }

    // 获取客制化选项状态
    @Override
    public Integer getOptionGlobalStatus(Long optionId) {
        MPJLambdaWrapper<CustomizeOption> wrapper = new MPJLambdaWrapper<CustomizeOption>()
                .select(CustomizeOption::getGlobalStatus)
                .eq(CustomizeOption::getId, optionId);
        return optionMapper.selectJoinOne(Integer.class,wrapper);
    }

    @Override
    public Integer getOptionStoreStatus(Long optionId, Long storeId) {
        MPJLambdaWrapper<CustomizeOptionStoreStatus> wrapper=new MPJLambdaWrapper<CustomizeOptionStoreStatus>()
                .select(CustomizeOptionStoreStatus::getStatus)
                .eq(CustomizeOptionStoreStatus::getOptionId,optionId)
                .eq(CustomizeOptionStoreStatus::getStoreId,storeId);
        Integer status=optionStatusMapper.selectJoinOne(Integer.class,wrapper);
        // 客制化选项门店状态默认可售
        return Objects.isNull(status)?CustomizeOptionStatus.AVAILABLE.getValue():status;
    }

    // 获取价格
    @Override
    public BigDecimal getProductPrice(Long id) {
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .select(Product::getPrice)
                .eq(Product::getId,id);
        return productMapper.selectJoinOne(BigDecimal.class,wrapper);
    }

    @Override
    public BigDecimal getCustomizeOptionPrice(Long id) {
        MPJLambdaWrapper<CustomizeOption> wrapper=new MPJLambdaWrapper<CustomizeOption>()
                .select(CustomizeOption::getPrice)
                .eq(CustomizeOption::getId,id);
        return optionMapper.selectJoinOne(BigDecimal.class,wrapper);
    }

    // 获取商品详情
    @Override
    public ProductModel getProductDetail(Long productId) {
        // 获取商品基础信息 + 分类
        MPJLambdaWrapper<Product> productWrapper=new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class,ProductModel.class)
                .eq(Product::getId,productId)
                // 商品分类
                .leftJoin(ProductCategory.class,ProductCategory::getId,Product::getCategoryId)
                .selectFunc("coalesce(%s,\"未知\")",arg ->arg
                                .accept(ProductCategory::getName),
                        ProductModel::getCategoryText);
        ProductModel product=productMapper.selectJoinOne(ProductModel.class,productWrapper);
        // 获取客制化项目 - 只返回可用项目
        MPJLambdaWrapper<CustomizeItem> itemWrapper=new MPJLambdaWrapper<CustomizeItem>()
                .selectAsClass(CustomizeItem.class, CustomizeItemModel.class)
                .eq(CustomizeItem::getProductId,productId)
                .eq(CustomizeItem::getStatus,CustomizeItemStatus.AVAILABLE.getValue())
                .orderByAsc(CustomizeItem::getSort);
        List<CustomizeItemModel> items=itemMapper.selectJoinList(CustomizeItemModel.class,itemWrapper);
        // 获取客制化选项
        for (CustomizeItemModel item:items){
            MPJLambdaWrapper<CustomizeOption> optionWrapper=new MPJLambdaWrapper<CustomizeOption>()
                    .selectAsClass(CustomizeOption.class, CustomizeOptionModel.class)
                    .eq(CustomizeOption::getItemId,item.getId())
                    .orderByAsc(CustomizeOption::getSort);
            List<CustomizeOptionModel> options=optionMapper.selectJoinList(CustomizeOptionModel.class,optionWrapper);
            item.setOptions(options);
        }
        product.setCustomize(items);
        return product;
    }

    @Override
    public ProductModel getProductDetail(Long productId, Long storeId) {
        // 获取商品基础信息 + 分类 + 门店状态
        MPJLambdaWrapper<Product> productWrapper=new MPJLambdaWrapper<Product>()
                .selectAsClass(Product.class,ProductModel.class)
                .eq(Product::getId,productId)
                // 商品分类
                .leftJoin(ProductCategory.class,ProductCategory::getId,Product::getCategoryId)
                .selectFunc("coalesce(%s,\"未知\")",arg ->arg
                                .accept(ProductCategory::getName),
                        ProductModel::getCategoryText)
                // 门店状态
                .leftJoin(ProductStoreStatus.class,"ps", on -> on
                        .eq(ProductStoreStatus::getProductId,Product::getId)
                        .eq(ProductStoreStatus::getStoreId,storeId))
                .selectFunc("coalesce(%s,3)",arg ->arg
                                .accept(ProductStoreStatus::getStatus),
                        ProductModel::getStoreStatus);
        ProductModel product=productMapper.selectJoinOne(ProductModel.class,productWrapper);
        // 获取客制化项目 - 只返回可用项目
        MPJLambdaWrapper<CustomizeItem> itemWrapper=new MPJLambdaWrapper<CustomizeItem>()
                .selectAsClass(CustomizeItem.class, CustomizeItemModel.class)
                .eq(CustomizeItem::getProductId,productId)
                .eq(CustomizeItem::getStatus, CustomizeItemStatus.AVAILABLE.getValue())
                .orderByAsc(CustomizeItem::getSort);
        List<CustomizeItemModel> items=itemMapper.selectJoinList(CustomizeItemModel.class,itemWrapper);
        // 获取客制化选项
        for (CustomizeItemModel item:items){
            MPJLambdaWrapper<CustomizeOption> optionWrapper=new MPJLambdaWrapper<CustomizeOption>()
                    .selectAsClass(CustomizeOption.class, CustomizeOptionModel.class)
                    .eq(CustomizeOption::getItemId,item.getId())
                    .orderByAsc(CustomizeOption::getSort)
                    // 门店状态
                    .leftJoin(CustomizeOptionStoreStatus.class,"os", on -> on
                            .eq(CustomizeOptionStoreStatus::getOptionId,CustomizeOption::getId)
                            .eq(CustomizeOptionStoreStatus::getStoreId,storeId))
                    .selectFunc("coalesce(%s,1)",arg ->arg
                                    .accept(CustomizeOptionStoreStatus::getStatus),
                            CustomizeOptionModel::getStoreStatus);
            List<CustomizeOptionModel> options=optionMapper.selectJoinList(CustomizeOptionModel.class,optionWrapper);
            item.setOptions(options);
        }
        product.setCustomize(items);
        return product;
    }
}

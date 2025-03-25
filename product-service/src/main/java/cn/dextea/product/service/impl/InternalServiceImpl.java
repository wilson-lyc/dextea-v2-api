package cn.dextea.product.service.impl;

import cn.dextea.common.code.CustomizeOptionStatus;
import cn.dextea.common.code.ProductStatus;
import cn.dextea.common.pojo.CustomizeOption;
import cn.dextea.common.pojo.CustomizeOptionStoreStatus;
import cn.dextea.common.pojo.Product;
import cn.dextea.common.pojo.ProductStoreStatus;
import cn.dextea.product.mapper.*;
import cn.dextea.product.service.InternalService;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class InternalServiceImpl implements InternalService {
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductStatusMapper productStatusMapper;
    @Resource
    private CategoryMapper categoryMapper;
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
        return categoryMapper.selectById(id)!=null;
    }

    @Override
    public boolean isCustomizeItemIdValid(Long id) {
        return itemMapper.selectById(id) != null;
    }

    @Override
    public boolean isCustomizeOptionIdValid(Long id) {
        return optionMapper.selectById(id)!=null;
    }

    @Override
    public Integer getProductStoreStatus(Long productId, Long storeId) {
        MPJLambdaWrapper<ProductStoreStatus> storeWrapper = new MPJLambdaWrapper<ProductStoreStatus>()
                .eq(ProductStoreStatus::getProductId,productId)
                .eq(ProductStoreStatus::getStoreId,storeId)
                .select(ProductStoreStatus::getStatus);
        Integer storeStatus=productStatusMapper.selectJoinOne(Integer.class,storeWrapper);
        return Objects.isNull(storeStatus)?ProductStatus.STORE_FORBIDDEN.getValue():storeStatus;
    }

    @Override
    public Integer getProductGlobalStatus(Long productId) {
        MPJLambdaWrapper<Product> globalWrapper = new MPJLambdaWrapper<Product>()
                .select(Product::getGlobalStatus)
                .eq(Product::getId, productId);
        return productMapper.selectJoinOne(Integer.class,globalWrapper);
    }

    @Override
    public Product getProductById(Long productId) {
        return productMapper.selectById(productId);
    }

    @Override
    public Product getProductById(Long productId, Long storeId) {
        Product product=getProductById(productId);
        if(Objects.nonNull(storeId))
            product.setStoreStatus(getProductStoreStatus(productId,storeId));
        return product;
    }

    @Override
    public Integer getCustomizeOptionGlobalStatus(Long optionId) {
        MPJLambdaWrapper<CustomizeOption> globalWrapper = new MPJLambdaWrapper<CustomizeOption>()
                .select(CustomizeOption::getGlobalStatus)
                .eq(CustomizeOption::getId, optionId);
        return optionMapper.selectJoinOne(Integer.class,globalWrapper);
    }

    @Override
    public Integer getCustomizeOptionStoreStatus(Long optionId, Long storeId) {
        MPJLambdaWrapper<CustomizeOptionStoreStatus> wrapper=new MPJLambdaWrapper<CustomizeOptionStoreStatus>()
                .select(CustomizeOptionStoreStatus::getStatus)
                .eq(CustomizeOptionStoreStatus::getOptionId,optionId)
                .eq(CustomizeOptionStoreStatus::getStoreId,storeId);
        Integer status=optionStatusMapper.selectJoinOne(Integer.class,wrapper);
        return Objects.isNull(status)?CustomizeOptionStatus.AVAILABLE.getValue():status;
    }
}

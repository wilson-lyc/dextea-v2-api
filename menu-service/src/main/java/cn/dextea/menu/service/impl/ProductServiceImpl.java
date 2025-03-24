package cn.dextea.menu.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.pojo.Product;
import cn.dextea.common.pojo.ProductStoreStatus;
import cn.dextea.menu.dto.ProductListDTO;
import cn.dextea.menu.feign.MenuFeign;
import cn.dextea.menu.feign.ProductFeign;
import cn.dextea.menu.mapper.MenuProductMapper;
import cn.dextea.common.pojo.MenuProduct;
import cn.dextea.menu.service.ProductService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private MenuProductMapper menuProductMapper;
    @Autowired
    private ProductFeign productFeign;
    @Autowired
    private MenuFeign menuFeign;

    @Override
    public ApiResponse menuBindProduct(Long groupId, Long productId, Integer sort) {
        // 校验ID
        if(!productFeign.isProductIdValid(productId)){
            return ApiResponse.badRequest("商品不存在");
        }
        if(!menuFeign.isGroupIdValid(groupId)){
            return ApiResponse.badRequest("分组不存在");
        }
        // 检查是否已经绑定
        MPJLambdaWrapper<MenuProduct> wrapper=new MPJLambdaWrapper<MenuProduct>()
                .eq(MenuProduct::getGroupId,groupId)
                .eq(MenuProduct::getProductId,productId);
        if(menuProductMapper.exists(wrapper)){
            return ApiResponse.badRequest("已绑定");
        }
        // 绑定商品
        MenuProduct menuProduct=MenuProduct.builder()
                .groupId(groupId)
                .productId(productId)
                .sort(sort)
                .build();
        menuProductMapper.insert(menuProduct);
        return ApiResponse.success("绑定成功");
    }

    @Override
    public ApiResponse menuUnbindProduct(Long groupId, Long productId) {
        // 校验ID
        if(!productFeign.isProductIdValid(productId)){
            return ApiResponse.badRequest("商品不存在");
        }
        if(!menuFeign.isGroupIdValid(groupId)){
            return ApiResponse.badRequest("分组不存在");
        }
        // 更新db
        MPJLambdaWrapper<MenuProduct> wrapper=new MPJLambdaWrapper<MenuProduct>()
                .eq(MenuProduct::getGroupId,groupId)
                .eq(MenuProduct::getProductId,productId);
        if(menuProductMapper.delete(wrapper)==0){
            return ApiResponse.badRequest("未绑定");
        }
        return ApiResponse.success("解绑成功");
    }

    @Override
    public ApiResponse getMenuBindProductInfo(Long groupId, Long productId) {
        // 校验ID
        if(!productFeign.isProductIdValid(productId)){
            return ApiResponse.badRequest("商品不存在");
        }
        if(!menuFeign.isGroupIdValid(groupId)){
            return ApiResponse.badRequest("分组不存在");
        }
        // 查询db
        MPJLambdaWrapper<MenuProduct> wrapper=new MPJLambdaWrapper<MenuProduct>()
                .eq(MenuProduct::getProductId,productId)
                .eq(MenuProduct::getGroupId,groupId)
                .selectAll(MenuProduct.class);
        MenuProduct menuProduct= menuProductMapper.selectOne(wrapper);
        if (Objects.isNull(menuProduct)){
            return ApiResponse.badRequest("未绑定");
        }
        return ApiResponse.success(JSONObject.of("bindInfo",menuProduct));
    }

    @Override
    public ApiResponse updateMenuBindProductInfo(Long groupId, Long productId, Integer sort) {
        // 校验ID
        if(!productFeign.isProductIdValid(productId)){
            return ApiResponse.badRequest("商品不存在");
        }
        if(!menuFeign.isGroupIdValid(groupId)){
            return ApiResponse.badRequest("分组不存在");
        }
        // 更新db
        UpdateWrapper<MenuProduct> updateWrapper=new UpdateWrapper<MenuProduct>()
                .set("sort",sort)
                .eq("product_id",productId)
                .eq("group_id",groupId);
        if (menuProductMapper.update(updateWrapper)==0){
            return ApiResponse.badRequest("未绑定");
        }else{
            return ApiResponse.success("更新成功");
        }
    }

    @Override
    public ApiResponse getProductList(Long groupId) {
        MPJLambdaWrapper<MenuProduct> wrapper=new MPJLambdaWrapper<MenuProduct>()
                .eq(MenuProduct::getGroupId,groupId)
                .innerJoin(Product.class,Product::getId,MenuProduct::getProductId)
                .selectAsClass(Product.class,ProductListDTO.class)
                .selectAs(MenuProduct::getSort,ProductListDTO::getSort);
        List<ProductListDTO> products= menuProductMapper.selectJoinList(ProductListDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("count",products.size(),"products", products));
    }

    @Override
    public ApiResponse getProductList(Long storeId, Long groupId) {
        MPJLambdaWrapper<MenuProduct> wrapper=new MPJLambdaWrapper<MenuProduct>()
                .innerJoin(Product.class,Product::getId,MenuProduct::getProductId)
                .selectAsClass(Product.class,ProductListDTO.class)
                .selectAs(MenuProduct::getSort,ProductListDTO::getSort)
                .leftJoin(ProductStoreStatus.class,"ps", on -> on
                        .eq(ProductStoreStatus::getProductId,Product::getId)
                        .eq(ProductStoreStatus::getStoreId,storeId))
                .selectFunc("coalesce(%s,3)",arg ->arg
                                .accept(ProductStoreStatus::getStatus),
                        ProductListDTO::getStoreStatus)
                .eq(MenuProduct::getGroupId,groupId);
        List<ProductListDTO> products= menuProductMapper.selectJoinList(ProductListDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("count",products.size(),"products", products));
    }
}

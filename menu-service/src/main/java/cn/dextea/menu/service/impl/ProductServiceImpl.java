package cn.dextea.menu.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.menu.dto.ProductListDTO;
import cn.dextea.menu.mapper.MenuProductMapper;
import cn.dextea.menu.pojo.MenuProduct;
import cn.dextea.menu.pojo.Product;
import cn.dextea.menu.pojo.ProductStoreStatus;
import cn.dextea.menu.service.ProductService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
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

    @Override
    public ApiResponse menuBindProduct(Long groupId, Long productId, Integer sort) {
        // 检查是否已经绑定
        MPJLambdaWrapper<MenuProduct> wrapper=new MPJLambdaWrapper<MenuProduct>()
                .eq(MenuProduct::getGroupId,groupId)
                .eq(MenuProduct::getProductId,productId);
        if(menuProductMapper.exists(wrapper)){
            return ApiResponse.badRequest("分组已绑定该商品");
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
        MPJLambdaWrapper<MenuProduct> wrapper=new MPJLambdaWrapper<MenuProduct>()
                .eq(MenuProduct::getGroupId,groupId)
                .eq(MenuProduct::getProductId,productId);
        if(menuProductMapper.delete(wrapper)==0){
            return ApiResponse.badRequest("商品未绑定");
        }
        return ApiResponse.success("解绑成功");
    }

    @Override
    public ApiResponse getMenuBindProductInfo(Long groupId, Long productId) {
        MPJLambdaWrapper<MenuProduct> wrapper=new MPJLambdaWrapper<MenuProduct>()
                .eq(MenuProduct::getProductId,productId)
                .eq(MenuProduct::getGroupId,groupId)
                .selectAll(MenuProduct.class);
        MenuProduct menuProduct= menuProductMapper.selectOne(wrapper);
        if (Objects.isNull(menuProduct)){
            return ApiResponse.badRequest("商品未绑定");
        }
        return ApiResponse.success(JSONObject.of("bindInfo",menuProduct.getSort()));
    }

    @Override
    public ApiResponse updateMenuBindProductInfo(Long groupId, Long productId, Integer sort) {
        UpdateWrapper<MenuProduct> updateWrapper=new UpdateWrapper<MenuProduct>()
                .set("sort",sort)
                .eq("product_id",productId)
                .eq("group_id",groupId);
        if (menuProductMapper.update(updateWrapper)==0){
            return ApiResponse.badRequest("商品未绑定");
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

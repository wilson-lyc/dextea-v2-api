package cn.dextea.menu.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.feign.MenuFeign;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.model.product.ProductModel;
import cn.dextea.menu.pojo.Menu;
import cn.dextea.menu.pojo.MenuGroup;
import cn.dextea.menu.pojo.MenuProduct;
import cn.dextea.menu.mapper.MenuMapper;
import cn.dextea.menu.service.ProductService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private MenuMapper menuMapper;
    @Resource
    private MenuFeign menuFeign;
    @Resource
    private ProductFeign productFeign;

    @Override
    public ApiResponse addProduct(Long menuId, String groupId, Long productId, Integer sort){
        Menu menu=menuMapper.selectById(menuId);
        if(Objects.isNull(menu))
            throw new IllegalArgumentException("menuId错误");
        MenuGroup menuGroup=menu.getMenuGroup(groupId);
        if(Objects.isNull(menuGroup))
            throw new IllegalArgumentException("groupId错误");
        if(!productFeign.isProductIdValid(productId))
            throw new IllegalArgumentException("productId错误");
        if(menuGroup.hasProduct(productId))
            return ApiResponse.badRequest("商品已存在");
        // 添加商品
        menuGroup.getContent().add(new MenuProduct(productId,sort));
        // 排序
        menuGroup.sortContent();
        // 更新menu
        menuMapper.updateById(menu);
        return ApiResponse.success("商品已添加至菜单");
    }

    @Override
    public ApiResponse deleteProduct(Long menuId, String groupId, Long productId) {
        Menu menu=menuMapper.selectById(menuId);
        if(Objects.isNull(menu))
            throw new IllegalArgumentException("menuId错误");
        MenuGroup menuGroup=menu.getMenuGroup(groupId);
        if(Objects.isNull(menuGroup))
            throw new IllegalArgumentException("groupId错误");
        if(!menuGroup.hasProduct(productId))
            throw new IllegalArgumentException("分组内不存在该商品");
        // 删除
        menuGroup.deleteProduct(productId);
        // 更新menu
        menuMapper.updateById(menu);
        return ApiResponse.success("商品已从菜单删除");
    }

    @Override
    public ApiResponse getProductList(Long menuId, String groupId) {
        Menu menu=menuMapper.selectById(menuId);
        if(Objects.isNull(menu))
            throw new IllegalArgumentException("menuId错误");
        MenuGroup menuGroup=menu.getMenuGroup(groupId);
        if(Objects.isNull(menuGroup))
            throw new IllegalArgumentException("groupId错误");
        JSONArray products=new JSONArray();
        JSONArray errors=new JSONArray();
        for (MenuProduct item:menuGroup.getContent()){
            ProductModel product=productFeign.getProductDetail(item.getId());
            if (Objects.isNull(product))
                errors.add(String.format("商品id=%d不存在",item.getId()));
            else
                products.add(JSONObject.of(
                        "id",product.getId(),
                        "name",product.getName(),
                        "price",product.getPrice(),
                        "sort",item.getSort()));
        }
        return ApiResponse.success(JSONObject.of(
                "products",products,
                "errors",errors));
    }

    @Override
    public ApiResponse getProductInfo(Long menuId, String groupId, Long productId) {
        Menu menu=menuMapper.selectById(menuId);
        if(Objects.isNull(menu))
            throw new IllegalArgumentException("menuId错误");
        MenuGroup menuGroup=menu.getMenuGroup(groupId);
        if(Objects.isNull(menuGroup))
            throw new IllegalArgumentException("groupId错误");
        if(!menuGroup.hasProduct(productId))
            throw new IllegalArgumentException("分组内不存在该商品");
        MenuProduct menuProduct=menuGroup.getProduct(productId);
        ProductModel product=productFeign.getProductDetail(productId);
        JSONObject productJson=JSONObject.of(
                "id",product.getId(),
                "name",product.getName(),
                "price",product.getPrice(),
                "sort",menuProduct.getSort()
        );
        return ApiResponse.success(JSONObject.of("product",productJson));
    }

    @Override
    public ApiResponse updateProductInfo(Long menuId, String groupId, Long productId, Integer sort) {
        Menu menu=menuMapper.selectById(menuId);
        if(Objects.isNull(menu))
            throw new IllegalArgumentException("menuId错误");
        MenuGroup menuGroup=menu.getMenuGroup(groupId);
        if(Objects.isNull(menuGroup))
            throw new IllegalArgumentException("groupId错误");
        if(!menuGroup.hasProduct(productId))
            throw new IllegalArgumentException("分组内不存在该商品");
        MenuProduct menuProduct=menuGroup.getProduct(productId);
        // 更新数据
        menuProduct.setSort(sort);
        // 排序
        menuGroup.sortContent();
        // 更新menu
        menuMapper.updateById(menu);
        return ApiResponse.success("更新成功");
    }
}

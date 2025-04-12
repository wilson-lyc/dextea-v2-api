package cn.dextea.menu.service.impl;

import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.menu.MenuProductModel;
import cn.dextea.common.model.product.ProductModel;
import cn.dextea.menu.code.MenuErrorCode;
import cn.dextea.menu.pojo.Menu;
import cn.dextea.menu.pojo.MenuGroup;
import cn.dextea.menu.pojo.MenuProduct;
import cn.dextea.menu.mapper.MenuMapper;
import cn.dextea.menu.service.ProductService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private MenuMapper menuMapper;
    @Resource
    private ProductFeign productFeign;

    @Override
    public DexteaApiResponse<Void> addProduct(Long menuId, String groupId, Long productId, Integer sort){
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu)) {
            return DexteaApiResponse.fail(MenuErrorCode.MENU_ID_ILLEGAL.getCode(),
                    MenuErrorCode.MENU_ID_ILLEGAL.getMsg());
        }
        MenuGroup menuGroup=menu.getMenuGroup(groupId);
        if(Objects.isNull(menuGroup)){
            return DexteaApiResponse.fail(MenuErrorCode.GROUP_ID_ILLEGAL.getCode(),
                    MenuErrorCode.GROUP_ID_ILLEGAL.getMsg());
        }
        if(!productFeign.isProductIdValid(productId)){
            return DexteaApiResponse.fail(MenuErrorCode.PRODUCT_ID_ILLEGAL.getCode(),
                    MenuErrorCode.PRODUCT_ID_ILLEGAL.getMsg());
        }
        if(menuGroup.hasProduct(productId)){
            return DexteaApiResponse.fail(MenuErrorCode.PRODUCT_EXISTED.getCode(),
                    MenuErrorCode.PRODUCT_EXISTED.getMsg());
        }
        // 添加商品
        menuGroup.getContent().add(new MenuProduct(productId,sort));
        // 排序
        menuGroup.sortContent();
        // 更新menu
        menuMapper.updateById(menu);
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<Void> deleteProduct(Long menuId, String groupId, Long productId) {
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu)) {
            return DexteaApiResponse.fail(MenuErrorCode.MENU_ID_ILLEGAL.getCode(),
                    MenuErrorCode.MENU_ID_ILLEGAL.getMsg());
        }
        MenuGroup menuGroup=menu.getMenuGroup(groupId);
        if(Objects.isNull(menuGroup)){
            return DexteaApiResponse.fail(MenuErrorCode.GROUP_ID_ILLEGAL.getCode(),
                    MenuErrorCode.GROUP_ID_ILLEGAL.getMsg());
        }
        if(!menuGroup.hasProduct(productId)){
            return DexteaApiResponse.fail(MenuErrorCode.PRODUCT_MISSED.getCode(),
                    MenuErrorCode.PRODUCT_MISSED.getMsg());
        }
        // 删除
        menuGroup.deleteProduct(productId);
        // 更新menu
        menuMapper.updateById(menu);
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<List<MenuProductModel>> getProductList(Long menuId, String groupId) {
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu)) {
            return DexteaApiResponse.fail(MenuErrorCode.MENU_ID_ILLEGAL.getCode(),
                    MenuErrorCode.MENU_ID_ILLEGAL.getMsg());
        }
        MenuGroup menuGroup=menu.getMenuGroup(groupId);
        if(Objects.isNull(menuGroup)){
            return DexteaApiResponse.fail(MenuErrorCode.GROUP_ID_ILLEGAL.getCode(),
                    MenuErrorCode.GROUP_ID_ILLEGAL.getMsg());
        }
        List<MenuProductModel> productList=new ArrayList<>();
        for (MenuProduct item:menuGroup.getContent()){
            ProductModel product=productFeign.getProductBase(item.getId());
            if (Objects.nonNull(product)){
                MenuProductModel productModel=MenuProductModel.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .price(product.getPrice())
                                .globalStatus(product.getGlobalStatus())
                                .sort(item.getSort())
                                .build();
                productList.add(productModel);
            }
        }
        return DexteaApiResponse.success(productList);
    }

    @Override
    public DexteaApiResponse<MenuProductModel> getProductInfo(Long menuId, String groupId, Long productId) {
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu)) {
            return DexteaApiResponse.fail(MenuErrorCode.MENU_ID_ILLEGAL.getCode(),
                    MenuErrorCode.MENU_ID_ILLEGAL.getMsg());
        }
        MenuGroup menuGroup=menu.getMenuGroup(groupId);
        if(Objects.isNull(menuGroup)){
            return DexteaApiResponse.fail(MenuErrorCode.GROUP_ID_ILLEGAL.getCode(),
                    MenuErrorCode.GROUP_ID_ILLEGAL.getMsg());
        }
        if(!menuGroup.hasProduct(productId)) {
            return DexteaApiResponse.fail(MenuErrorCode.PRODUCT_MISSED.getCode(),
                    MenuErrorCode.PRODUCT_MISSED.getMsg());
        }
        MenuProduct menuProduct=menuGroup.getProduct(productId);
        ProductModel product=productFeign.getProductDetail(productId);
        MenuProductModel menuProductModel=MenuProductModel.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .globalStatus(product.getGlobalStatus())
                        .sort(menuProduct.getSort())
                        .build();
        return DexteaApiResponse.success(menuProductModel);
    }

    @Override
    public DexteaApiResponse<Void> updateProductInfo(Long menuId, String groupId, Long productId, Integer sort) {
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu)) {
            return DexteaApiResponse.fail(MenuErrorCode.MENU_ID_ILLEGAL.getCode(),
                    MenuErrorCode.MENU_ID_ILLEGAL.getMsg());
        }
        MenuGroup menuGroup=menu.getMenuGroup(groupId);
        if(Objects.isNull(menuGroup)){
            return DexteaApiResponse.fail(MenuErrorCode.GROUP_ID_ILLEGAL.getCode(),
                    MenuErrorCode.GROUP_ID_ILLEGAL.getMsg());
        }
        if(!menuGroup.hasProduct(productId)) {
            return DexteaApiResponse.fail(MenuErrorCode.PRODUCT_MISSED.getCode(),
                    MenuErrorCode.PRODUCT_MISSED.getMsg());
        }
        MenuProduct menuProduct=menuGroup.getProduct(productId);
        // 更新数据
        menuProduct.setSort(sort);
        // 排序
        menuGroup.sortContent();
        // 更新menu
        menuMapper.updateById(menu);
        return DexteaApiResponse.success();
    }
}

package cn.dextea.menu.service.impl;

import cn.dextea.common.feign.MenuFeign;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.feign.StoreFeign;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.menu.MenuGroupModel;
import cn.dextea.common.model.menu.MenuModel;
import cn.dextea.common.model.menu.MenuProductModel;
import cn.dextea.common.model.product.ProductModel;
import cn.dextea.menu.code.MenuErrorCode;
import cn.dextea.menu.pojo.Menu;
import cn.dextea.menu.pojo.MenuGroup;
import cn.dextea.menu.pojo.MenuProduct;
import cn.dextea.menu.model.menu.*;
import cn.dextea.menu.mapper.MenuMapper;
import cn.dextea.menu.service.MenuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class MenuServiceImpl implements MenuService {
    @Resource
    private MenuMapper menuMapper;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private MenuFeign menuFeign;
    @Resource
    private StoreFeign storeFeign;

    @Override
    public DexteaApiResponse<Void> createMenu(MenuCreateRequest data) {
        Menu menu=data.toMenu();
        menuMapper.insert(menu);
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<IPage<MenuModel>> getMenuList(int current, int size, MenuFilter filter) {
        MPJLambdaWrapper<Menu> wrapper=new MPJLambdaWrapper<Menu>()
                .selectAsClass(Menu.class,MenuModel.class)
                .eqIfExists(Menu::getId,filter.getId())
                .likeIfExists(Menu::getName,filter.getName());
        IPage<MenuModel> page=menuMapper.selectJoinPage(
                new Page<>(current, size),
                MenuModel.class,
                wrapper);
        if (current>page.getPages())
            page=menuMapper.selectJoinPage(
                    new Page<>(page.getPages(),size),
                    MenuModel.class,
                    wrapper);
        return DexteaApiResponse.success(page);
    }

    @Override
    public DexteaApiResponse<MenuModel> getMenuDetail(Long id, Long storeId){
        Menu menu=menuMapper.selectById(id);
        if (Objects.isNull(menu)) {
            return DexteaApiResponse.notFound(MenuErrorCode.MENU_NOT_FOUND.getCode(),
                    MenuErrorCode.MENU_NOT_FOUND.getMsg());
        }
        MenuModel menuModel=MenuModel.builder()
                .id(menu.getId())
                .name(menu.getName())
                .description(menu.getDescription())
                .createTime(menu.getCreateTime())
                .updateTime(menu.getUpdateTime())
                .build();
        List<MenuGroupModel> groupModelList=new ArrayList<>();
        for (MenuGroup group:menu.getContent()){
            MenuGroupModel menuGroupModel=MenuGroupModel.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .sort(group.getSort())
                    .build();
            // 遍历商品
            List<MenuProductModel> productModelList=new ArrayList<>();
            for (MenuProduct menuProduct:group.getContent()){
                ProductModel product=productFeign.getProductDetail(menuProduct.getId(),storeId);
                // 商品存在则可以返回给前端
                if (Objects.nonNull(product)) {
                    MenuProductModel menuProductModel = MenuProductModel.builder()
                            .id(product.getId())
                            .name(product.getName())
                            .description(product.getDescription())
                            .price(product.getPrice())
                            .cover(product.getCover())
                            .globalStatus(product.getGlobalStatus())
                            .storeStatus(product.getStoreStatus())
                            .sort(menuProduct.getSort())
                            .createTime(product.getCreateTime())
                            .updateTime(product.getUpdateTime())
                            .build();
                    productModelList.add(menuProductModel);
                }
            }
            menuGroupModel.setContent(productModelList);
            groupModelList.add(menuGroupModel);
        }
        menuModel.setContent(groupModelList);
        return DexteaApiResponse.success(menuModel);
    }

    @Override
    public DexteaApiResponse<MenuModel> getMenuBase(Long id){
        MPJLambdaWrapper<Menu>wrapper=new MPJLambdaWrapper<Menu>()
                .eq(Menu::getId,id)
                .selectAsClass(Menu.class, MenuModel.class);
        MenuModel menu=menuMapper.selectJoinOne(MenuModel.class,wrapper);
        if (Objects.isNull(menu)) {
            return DexteaApiResponse.notFound(MenuErrorCode.MENU_NOT_FOUND.getCode(),
                    MenuErrorCode.MENU_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success(menu);
    }

    @Override
    public DexteaApiResponse<Void> updateMenuBase(Long id, MenuUpdateBaseRequest data){
        Menu menu=data.toMenu(id);
        if(menuMapper.updateById(menu)==0){
            return DexteaApiResponse.notFound(MenuErrorCode.MENU_NOT_FOUND.getCode(),
                    MenuErrorCode.MENU_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<MenuBindResponse> storeBindMenu(Long menuId, List<Long> storeIds) {
        List<Long> success=new ArrayList<>();
        List<Long> fail=new ArrayList<>();
        if(!menuFeign.isMenuIdValid(menuId)) {
            return DexteaApiResponse.fail(MenuErrorCode.MENU_ID_ILLEGAL.getCode(),
                    MenuErrorCode.MENU_ID_ILLEGAL.getMsg());
        }
        for (Long storeId:storeIds){
            if(storeFeign.storeBindMenu(storeId,menuId))
                success.add(storeId);
            else
                fail.add(storeId);
        }
        MenuBindResponse response=new MenuBindResponse(success,fail);
        return DexteaApiResponse.success(response);
    }
}

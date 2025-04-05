package cn.dextea.menu.service.impl;

import cn.dextea.common.code.ProductStatus;
import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.feign.StoreFeign;
import cn.dextea.common.model.menu.MenuGroupModel;
import cn.dextea.common.model.menu.MenuModel;
import cn.dextea.common.model.menu.MenuProductModel;
import cn.dextea.common.model.product.ProductModel;
import cn.dextea.menu.pojo.Menu;
import cn.dextea.menu.pojo.MenuGroup;
import cn.dextea.menu.pojo.MenuProduct;
import cn.dextea.menu.mapper.MenuMapper;
import cn.dextea.menu.service.CustomerService;
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
public class CustomerServiceImpl implements CustomerService {
    @Resource
    private MenuMapper menuMapper;
    @Resource
    private StoreFeign storeFeign;
    @Resource
    private ProductFeign productFeign;

    @Override
    public DexteaApiResponse<MenuModel> getStoreMenu(Long storeId) throws NotFoundException {
        // 获取menuId
        Long menuId=storeFeign.getStoreMenuId(storeId);
        if (Objects.isNull(menuId))
            throw new IllegalArgumentException("门店未绑定菜单");
        // 获取菜单
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu))
            throw new NotFoundException("菜单不存在");
        // 获取商品详情
        MenuModel menuModel=MenuModel.builder()
                .id(menu.getId())
                .name(menu.getName())
                .description(menu.getDescription())
                .createTime(menu.getCreateTime())
                .updateTime(menu.getUpdateTime())
                .build();
        // 遍历分组
        List<MenuGroupModel> menuGroups=new ArrayList<>();
        for (MenuGroup group:menu.getContent()){
            MenuGroupModel menuGroupModel=MenuGroupModel.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .sort(group.getSort())
                    .build();
            // 遍历商品
            List<MenuProductModel> menuProductModels=new ArrayList<>();
            for (MenuProduct menuProduct:group.getContent()){
                ProductModel product=productFeign.getProductDetail(menuProduct.getId(),storeId);
                if (Objects.nonNull(product) &&
                        product.getStatus()!=ProductStatus.GLOBAL_FORBIDDEN.getValue() &&
                        product.getStatus()!=ProductStatus.STORE_FORBIDDEN.getValue()) {
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
                    menuProductModels.add(menuProductModel);
                }
            }
            if (!menuProductModels.isEmpty()){
                menuGroupModel.setContent(menuProductModels);
                menuGroups.add(menuGroupModel);
            }
        }
        menuModel.setContent(menuGroups);
        return DexteaApiResponse.success(menuModel);
    }
}

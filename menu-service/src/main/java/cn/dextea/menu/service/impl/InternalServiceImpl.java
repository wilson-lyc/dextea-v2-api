package cn.dextea.menu.service.impl;

import cn.dextea.common.code.ProductStatus;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.model.menu.MenuGroupModel;
import cn.dextea.common.model.menu.MenuModel;
import cn.dextea.common.model.menu.MenuProductModel;
import cn.dextea.common.model.product.ProductModel;
import cn.dextea.menu.mapper.MenuMapper;
import cn.dextea.menu.pojo.Menu;
import cn.dextea.menu.pojo.MenuGroup;
import cn.dextea.menu.pojo.MenuProduct;
import cn.dextea.menu.service.InternalService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class InternalServiceImpl implements InternalService {
    @Resource
    private MenuMapper menuMapper;
    @Resource
    private ProductFeign productFeign;
    @Override
    public MenuModel getMenuDetail(Long id, String mode, Long storeId){
        // 获取菜单
        Menu menu=menuMapper.selectById(id);
        if (Objects.isNull(menu)) {
            return null;
        }
        MenuModel menuModel=MenuModel.builder()
                .id(menu.getId())
                .name(menu.getName())
                .description(menu.getDescription())
                .createTime(menu.getCreateTime())
                .updateTime(menu.getUpdateTime())
                .build();
        // 遍历分组
        List<MenuGroupModel> menuGroupModelList=new ArrayList<>();
        for (MenuGroup group:menu.getContent()){
            MenuGroupModel menuGroupModel=MenuGroupModel.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .sort(group.getSort())
                    .build();
            // 遍历商品
            List<MenuProductModel> menuProductModelList=new ArrayList<>();
            for (MenuProduct product:group.getContent()){
                ProductModel productModel;
                if(Objects.nonNull(storeId)){
                    productModel=productFeign.getProductBase(product.getId(),storeId);
                } else{
                    productModel=productFeign.getProductBase(product.getId());
                }
                if (Objects.nonNull(productModel) && (
                        (mode.equals("active_only") &&
                                !productModel.getStatus().equals(ProductStatus.GLOBAL_FORBIDDEN.getValue()) &&
                                !productModel.getStatus().equals(ProductStatus.STORE_FORBIDDEN.getValue())) ||
                        (mode.equals("all")))) {
                    MenuProductModel menuProductModel = MenuProductModel.builder()
                            .id(product.getId())
                            .name(productModel.getName())
                            .description(productModel.getDescription())
                            .price(productModel.getPrice())
                            .cover(productModel.getCover())
                            .globalStatus(productModel.getGlobalStatus())
                            .storeStatus(productModel.getStoreStatus())
                            .sort(product.getSort())
                            .createTime(productModel.getCreateTime())
                            .updateTime(productModel.getUpdateTime())
                            .build();
                    menuProductModelList.add(menuProductModel);
                }
            }
            if(mode.equals("all")||(mode.equals("active_only")&&!menuProductModelList.isEmpty())){
                menuGroupModel.setContent(menuProductModelList);
                menuGroupModelList.add(menuGroupModel);
            }
        }
        menuModel.setContent(menuGroupModelList);
        return menuModel;
    }
    @Override
    public boolean isMenuIdValid(Long id) {
        return Objects.nonNull(menuMapper.selectById(id));
    }
}

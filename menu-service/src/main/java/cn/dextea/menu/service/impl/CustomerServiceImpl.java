package cn.dextea.menu.service.impl;

import cn.dextea.common.code.ProductStatus;
import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.feign.StoreFeign;
import cn.dextea.common.pojo.Menu;
import cn.dextea.common.pojo.MenuGroup;
import cn.dextea.common.pojo.MenuProduct;
import cn.dextea.common.pojo.Product;
import cn.dextea.menu.mapper.MenuMapper;
import cn.dextea.menu.service.CustomerService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

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
    public ApiResponse getStoreMenu(Long id) throws NotFoundException {
        Long menuId=storeFeign.getStoreMenuId(id);
        if (Objects.isNull(menuId))
            throw new IllegalArgumentException("门店未绑定菜单");
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu))
            throw new NotFoundException("菜单不存在");
        JSONArray menuContent=new JSONArray();
        for (MenuGroup group:menu.getContent()){
            JSONArray productArray=new JSONArray();
            for (MenuProduct menuProduct:group.getContent()){
                Product product=productFeign.getProductById(menuProduct.getId(),id);
                if (Objects.nonNull(product) &&
                        product.getStatus()!=ProductStatus.GLOBAL_FORBIDDEN.getValue()&&
                        product.getStatus()!= ProductStatus.STORE_FORBIDDEN.getValue()){
                    productArray.add(product);
                }
            }
            if (productArray.size() > 0){
                JSONObject groupJson=JSONObject.of(
                        "id",group.getId(),
                        "name",group.getName(),
                        "content",productArray
                );
                menuContent.add(groupJson);
            }
        }
        JSONObject menuJson=JSONObject.of(
                "id",menu.getId(),
                "name",menu.getName(),
                "content",menuContent);
        return ApiResponse.success(JSONObject.of("menu",menuJson));
    }
}

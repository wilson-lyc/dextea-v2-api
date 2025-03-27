package cn.dextea.menu.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.feign.MenuFeign;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.feign.StoreFeign;
import cn.dextea.common.pojo.Menu;
import cn.dextea.common.pojo.MenuGroup;
import cn.dextea.common.pojo.MenuProduct;
import cn.dextea.common.pojo.Product;
import cn.dextea.menu.dto.menu.*;
import cn.dextea.menu.mapper.MenuMapper;
import cn.dextea.menu.service.MenuService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

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
    public ApiResponse createMenu(MenuCreateDTO data) {
        Menu menu=data.toMenu();
        menuMapper.insert(menu);
        return ApiResponse.success("菜单创建成功");
    }

    private IPage<MenuListDTO> getMenuListPage(int current, int size, MPJLambdaWrapper<Menu> wrapper){
        IPage<MenuListDTO> page=menuMapper.selectJoinPage(
                new Page<>(current, size),
                MenuListDTO.class,
                wrapper);
        if (current>page.getPages())
            page=menuMapper.selectJoinPage(
                    new Page<>(page.getPages(),size),
                    MenuListDTO.class,
                    wrapper);
        return page;
    }

    @Override
    public ApiResponse getMenuList(int current, int size, MenuQueryDTO filter) {
        MPJLambdaWrapper<Menu> wrapper=new MPJLambdaWrapper<Menu>()
                .eqIfExists(Menu::getId,filter.getId())
                .likeIfExists(Menu::getName,filter.getName());
        return ApiResponse.success(JSONObject.from(getMenuListPage(current,size,wrapper)));
    }

    @Override
    public ApiResponse getMenuById(Long id, Long storeId) throws NotFoundException {
        Menu menu=menuMapper.selectById(id);
        if (Objects.isNull(menu))
            throw new NotFoundException("菜单不存在");
        JSONArray menuJson=new JSONArray();
        JSONArray errJson=new JSONArray();
        for(MenuGroup group:menu.getContent()){
            JSONObject groupJson=new JSONObject();
            groupJson.put("id",group.getId());
            groupJson.put("name",group.getName());
            JSONArray contentJson=new JSONArray();
            for(MenuProduct product:group.getContent()){
                Product productInfo=productFeign.getProductById(product.getId(),storeId);
                if (Objects.isNull(productInfo)){
                    errJson.add(String.format("商品id=%d不存在",product.getId()));
                }
                JSONObject productJson= JSONObject.from(productInfo);
                productJson.put("sort",product.getSort());
                contentJson.add(productJson);
            }
            groupJson.put("content",contentJson);
            menuJson.add(groupJson);
        }
        return ApiResponse.success(JSONObject.of(
                "menu",menuJson,
                "error",errJson));
    }

    @Override
    public ApiResponse getMenuBase(Long id) throws NotFoundException {
        MPJLambdaWrapper<Menu>wrapper=new MPJLambdaWrapper<Menu>()
                .eq(Menu::getId,id)
                .selectAsClass(Menu.class, MenuBaseDTO.class);
        MenuBaseDTO menu=menuMapper.selectJoinOne(MenuBaseDTO.class,wrapper);
        if (Objects.isNull(menu))
            throw new NotFoundException("菜单不存在");
        return ApiResponse.success(JSONObject.of("menu",menu));
    }

    @Override
    public ApiResponse updateMenuBase(Long id, MenuUpdateBaseDTO data) throws NotFoundException {
        Menu menu=data.toMenu(id);
        if(menuMapper.updateById(menu)==0)
            throw new NotFoundException("菜单不存在");
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse storeBindMenu(Long menuId, List<Long> storeIds) {
        JSONArray success=new JSONArray();
        JSONArray fail=new JSONArray();
        if(!menuFeign.isMenuIdValid(menuId))
            throw new IllegalArgumentException("menuId错误");
        for (Long storeId:storeIds){
            if(storeFeign.storeBindMenu(storeId,menuId))
                success.add(String.format("storeId=%d绑定成功",storeId));
            else
                fail.add(String.format("storeId=%d绑定失败",storeId));
        }
        return ApiResponse.success(JSONObject.of(
                "success",success,
                "fail",fail));
    }
}

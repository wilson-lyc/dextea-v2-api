package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.*;
import cn.dextea.product.mapper.MenuGroupMapper;
import cn.dextea.product.mapper.MenuMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.pojo.Menu;
import cn.dextea.product.pojo.MenuGroup;
import cn.dextea.product.pojo.MenuProduct;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.service.MenuService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class MenuServiceImpl implements MenuService {
    @Resource
    private MenuMapper menuMapper;
    @Resource
    private MenuGroupMapper menuGroupMapper;
    @Resource
    private ProductMapper productMapper;

    @Override
    public ApiResponse createMenu(MenuCreateDTO data) {
        Menu menu=data.toMenu();
        menuMapper.insert(menu);
        return ApiResponse.success("创建成功");
    }

    @Override
    public ApiResponse getMenuList(int current, int size, MenuQueryDTO filter) {
        QueryWrapper<Menu> wrapper=new QueryWrapper<>();
        if(filter.getId()!=null){
            wrapper.eq("id", filter.getId());
        }
        if(filter.getName()!=null){
            wrapper.like("name", filter.getName());
        }
        Page<Menu> page=new Page<>(current,size);
        page=menuMapper.selectPage(page,wrapper);
        if(page.getCurrent()>page.getPages()){
            page.setCurrent(page.getPages());
            page=menuMapper.selectPage(page,wrapper);
        }
        return ApiResponse.success(JSONObject.from(page));
    }

    @Override
    public ApiResponse getMenuBaseById(Long id) {
        Menu menu=menuMapper.selectById(id);
        if(menu==null){
            return ApiResponse.notFound(String.format("不存在 ID=%d 的菜单",id));
        }
        return ApiResponse.success(JSONObject.of("menu",menu));
    }

    @Override
    public ApiResponse updateMenu(Long id, MenuBaseUpdateDTO data) {
        Menu menu=data.toMenu();
        menu.setId(id);
        int count=menuMapper.updateById(menu);
        if(count==0){
            return ApiResponse.notFound(String.format("不存在 ID=%d 的菜单",id));
        }
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse getMenuById(Long id) {
        JSONArray menu=new JSONArray();
        // 查询菜单分组
        QueryWrapper<MenuGroup> groupWrapper=new QueryWrapper<>();
        groupWrapper.eq("menu_id", id);
        List<MenuGroup> menuGroups=menuGroupMapper.selectList(groupWrapper);
        // 查询商品
        for(MenuGroup group:menuGroups){
            JSONObject groupJson=new JSONObject();
            groupJson.put("groupId",group.getId());
            groupJson.put("groupName",group.getName());
            MPJLambdaWrapper<Product> productWrapper=new MPJLambdaWrapper<Product>()
                    .selectAll(Product.class)
                    .innerJoin(MenuProduct.class,MenuProduct::getProductId,Product::getId)
                    .eq(MenuProduct::getGroupId,group.getId());
            List<Product> products=productMapper.selectJoinList(productWrapper);
            groupJson.put("count",products.size());
            groupJson.put("products",products);
            menu.add(groupJson);
        }
        return ApiResponse.success(JSONObject.of("menu",menu));
    }
}

package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.*;
import cn.dextea.product.mapper.MenuMapper;
import cn.dextea.product.mapper.MenuTypeMapper;
import cn.dextea.product.pojo.Menu;
import cn.dextea.product.pojo.MenuType;
import cn.dextea.product.service.MenuService;
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
    private MenuTypeMapper menuTypeMapper;

    @Override
    public ApiResponse createMenu(MenuCreateDTO data) {
        Menu menu=data.toMenu();
        menuMapper.insert(menu);
        return ApiResponse.success("创建成功");
    }

    @Override
    public ApiResponse getMenuListByFilter(int current, int size, MenuFilterDTO filter) {
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
            return ApiResponse.notFound(String.format("菜单不存在，id=%d",id));
        }
        return ApiResponse.success(JSONObject.of("menu",menu));
    }

    @Override
    public ApiResponse updateMenuBase(Long id, MenuBaseUpdateDTO data) {
        Menu menu=data.toMenu();
        menu.setId(id);
        int count=menuMapper.updateById(menu);
        if(count==0){
            return ApiResponse.notFound(String.format("菜单不存在，id=%d",id));
        }
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse createMenuType(MenuTypeCreateDTO data) {
        MenuType menuType=data.toMenuType();
        menuTypeMapper.insert(menuType);
        return ApiResponse.success("创建成功");
    }

    @Override
    public ApiResponse getMenuTypeBaseById(Long id) {
        MenuType menuType=menuTypeMapper.selectById(id);
        if (menuType==null){
            return ApiResponse.notFound(String.format("资源不存在，id=%d",id));
        }
        return ApiResponse.success(JSONObject.of("menuType",menuType));
    }

    @Override
    public ApiResponse getMenuTypeList(Long menuId) {
        MPJLambdaWrapper<MenuType> wrapper=new MPJLambdaWrapper<MenuType>()
                .selectAll(MenuType.class)
                .innerJoin(Menu.class,Menu::getId,MenuType::getMenuId)
                .eq(Menu::getId,menuId);
        List<MenuType> list=menuTypeMapper.selectList(wrapper);
        return ApiResponse.success(JSONObject.of("typeList",list));
    }

    @Override
    public ApiResponse updateMenuTypeBase(Long id, MenuTypeUpdateDTO data) {
        MenuType menuType=data.toMenuType();
        menuType.setId(id);
        int num=menuTypeMapper.updateById(menuType);
        if (num==0){
            return ApiResponse.notFound(String.format("资源不存在，id=%d",id));
        }
        return ApiResponse.success("更新成功");
    }
}

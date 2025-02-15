package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.MenuTypeCreateDTO;
import cn.dextea.product.dto.MenuTypeUpdateDTO;
import cn.dextea.product.mapper.MenuTypeMapper;
import cn.dextea.product.pojo.Menu;
import cn.dextea.product.pojo.MenuType;
import cn.dextea.product.service.MenuTypeService;
import com.alibaba.fastjson2.JSONObject;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class MenuTypeServiceImpl implements MenuTypeService {
    @Resource
    private MenuTypeMapper menuTypeMapper;

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
    public ApiResponse getMenuTypeListByMenuId(Long menuId) {
        MPJLambdaWrapper<MenuType> wrapper=new MPJLambdaWrapper<MenuType>()
                .selectAll(MenuType.class)
                .innerJoin(Menu.class,Menu::getId,MenuType::getMenuId)
                .orderByAsc(MenuType::getSort)
                .eq(Menu::getId,menuId);
        List<MenuType> list=menuTypeMapper.selectList(wrapper);
        return ApiResponse.success(JSONObject.of("typeList",list));
    }

    @Override
    public ApiResponse updateMenuTypeBaseById(Long id, MenuTypeUpdateDTO data) {
        MenuType menuType=data.toMenuType();
        menuType.setId(id);
        int num=menuTypeMapper.updateById(menuType);
        if (num==0){
            return ApiResponse.notFound(String.format("资源不存在，id=%d",id));
        }
        return ApiResponse.success("更新成功");
    }
}

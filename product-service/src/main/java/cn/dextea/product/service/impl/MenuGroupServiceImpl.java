package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.MenuGroupCreateDTO;
import cn.dextea.product.dto.MenuTypeUpdateDTO;
import cn.dextea.product.mapper.MenuGroupMapper;
import cn.dextea.product.pojo.Menu;
import cn.dextea.product.pojo.MenuGroup;
import cn.dextea.product.service.MenuGroupService;
import com.alibaba.fastjson2.JSONObject;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class MenuGroupServiceImpl implements MenuGroupService {
    @Resource
    private MenuGroupMapper menuGroupMapper;

    @Override
    public ApiResponse createMenuGroup(MenuGroupCreateDTO data) {
        MenuGroup menuGroup = data.toMenuGroup();
        menuGroupMapper.insert(menuGroup);
        return ApiResponse.success("创建成功");
    }

    @Override
    public ApiResponse getMenuGroupById(Long id) {
        MenuGroup menuGroup = menuGroupMapper.selectById(id);
        if (menuGroup ==null){
            return ApiResponse.notFound(String.format("不存在 ID=%d 的菜单分组",id));
        }
        return ApiResponse.success(JSONObject.of("group", menuGroup));
    }

    @Override
    public ApiResponse getMenuGroupList(Long menuId) {
        MPJLambdaWrapper<MenuGroup> wrapper=new MPJLambdaWrapper<MenuGroup>()
                .selectAll(MenuGroup.class)
                .innerJoin(Menu.class,Menu::getId, MenuGroup::getMenuId)
                .orderByAsc(MenuGroup::getSort)
                .eq(Menu::getId,menuId);
        List<MenuGroup> list= menuGroupMapper.selectList(wrapper);
        return ApiResponse.success(JSONObject.of("groups",list));
    }

    @Override
    public ApiResponse updateMenuGroup(Long id, MenuTypeUpdateDTO data) {
        MenuGroup menuGroup =data.toMenuType();
        menuGroup.setId(id);
        int num= menuGroupMapper.updateById(menuGroup);
        if (num==0){
            return ApiResponse.notFound(String.format("不存在 ID=%d 的菜单分组",id));
        }
        return ApiResponse.success("更新成功");
    }
}

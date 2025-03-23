package cn.dextea.menu.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.menu.dto.MenuEditDTO;
import cn.dextea.menu.dto.MenuQueryDTO;
import cn.dextea.menu.mapper.MenuMapper;
import cn.dextea.menu.pojo.Menu;
import cn.dextea.menu.service.MenuService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class MenuServiceImpl implements MenuService {
    @Resource
    private MenuMapper menuMapper;

    @Override
    public ApiResponse createMenu(MenuEditDTO data) {
        Menu menu=data.toMenu();
        menuMapper.insert(menu);
        return ApiResponse.success("菜单创建成功");
    }

    @Override
    public ApiResponse getMenuList(int current, int size, MenuQueryDTO filter) {
        MPJLambdaWrapper<Menu> wrapper=new MPJLambdaWrapper<Menu>()
                .selectAll(Menu.class)
                .eqIfExists(Menu::getId,filter.getId())
                .likeIfExists(Menu::getName,filter.getName());
        IPage<Menu> page=menuMapper.selectJoinPage(
                new Page<>(current,size),
                Menu.class,
                wrapper);
        if(page.getCurrent()>page.getPages()){
            page=menuMapper.selectJoinPage(
                    new Page<>(page.getPages(),size),
                    Menu.class,
                    wrapper);
        }
        return ApiResponse.success(JSONObject.from(page));
    }

    @Override
    public ApiResponse getMenuInfo(Long id) {
        Menu menu=menuMapper.selectById(id);
        if(Objects.isNull(menu)){
            return ApiResponse.notFound("不存在该菜单");
        }
        return ApiResponse.success(JSONObject.of("menu",menu));
    }

    @Override
    public ApiResponse updateMenuInfo(Long id, MenuEditDTO data) {
        Menu menu=data.toMenu();
        menu.setId(id);
        if(menuMapper.updateById(menu)==0){
            return ApiResponse.notFound("不存在该菜单");
        }
        return ApiResponse.success("更新成功");
    }
}

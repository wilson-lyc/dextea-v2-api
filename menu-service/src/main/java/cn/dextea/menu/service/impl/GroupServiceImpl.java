package cn.dextea.menu.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.menu.dto.GroupEditDTO;
import cn.dextea.menu.feign.MenuFeign;
import cn.dextea.menu.mapper.MenuGroupMapper;
import cn.dextea.menu.pojo.MenuGroup;
import cn.dextea.menu.service.GroupService;
import com.alibaba.fastjson2.JSONObject;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class GroupServiceImpl implements GroupService {
    @Resource
    private MenuGroupMapper menuGroupMapper;
    @Resource
    private MenuFeign menuFeign;

    @Override
    public ApiResponse createGroup(Long menuId, GroupEditDTO data) {
        // 校验菜单ID
        if(!menuFeign.isMenuIdValid(menuId))
            return ApiResponse.badRequest("菜单不存在");
        // 插入db
        MenuGroup menuGroup = data.toMenuGroup();
        menuGroup.setMenuId(menuId);
        menuGroupMapper.insert(menuGroup);
        return ApiResponse.success("分组创建成功");
    }

    @Override
    public ApiResponse getGroupList(Long menuId) {
        MPJLambdaWrapper<MenuGroup> wrapper=new MPJLambdaWrapper<MenuGroup>()
                .selectAll(MenuGroup.class)
                .eq(MenuGroup::getMenuId,menuId)
                .orderByAsc(MenuGroup::getSort);
        List<MenuGroup> list= menuGroupMapper.selectJoinList(MenuGroup.class,wrapper);
        return ApiResponse.success(JSONObject.of("groups",list));
    }

    @Override
    public ApiResponse getGroupInfo(Long groupId) {
        MenuGroup menuGroup = menuGroupMapper.selectById(groupId);
        if (Objects.isNull(menuGroup)){
            return ApiResponse.notFound("菜单分组不存在");
        }
        return ApiResponse.success(JSONObject.of("group", menuGroup));
    }

    @Override
    public ApiResponse updateGroupInfo(Long groupId, GroupEditDTO data) {
        MenuGroup menuGroup =data.toMenuGroup();
        menuGroup.setId(groupId);
        if (menuGroupMapper.updateById(menuGroup)==0){
            return ApiResponse.notFound("菜单分组不存在");
        }
        return ApiResponse.success("更新成功");
    }
}

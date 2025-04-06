package cn.dextea.menu.service.impl;

import cn.dextea.common.model.common.ApiResponse;
import cn.dextea.menu.pojo.Menu;
import cn.dextea.menu.dto.group.GroupBaseDTO;
import cn.dextea.menu.dto.group.GroupCreateDTO;
import cn.dextea.menu.dto.group.GroupListDTO;
import cn.dextea.menu.dto.group.GroupUpdateBaseDTO;
import cn.dextea.menu.mapper.MenuMapper;
import cn.dextea.menu.pojo.MenuGroup;
import cn.dextea.menu.service.GroupService;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Lai Yongchao
 */
@Service
public class GroupServiceImpl implements GroupService {
    @Resource
    private MenuMapper menuMapper;

    @Override
    public ApiResponse createGroup(Long menuId, GroupCreateDTO data) {
        // 读取menu
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu))
            throw new IllegalArgumentException("menuId错误");
        // 创建分组
        MenuGroup menuGroup = data.toMenuGroup();
        menu.getContent().add(menuGroup);
        // 排序
        menu.sortContent();
        // 更新menu
        menuMapper.updateById(menu);
        return ApiResponse.success("创建成功");
    }

    @Override
    public ApiResponse getGroupList(Long menuId) {
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu))
            throw new IllegalArgumentException("menuId错误");
        List<GroupListDTO> groupList = menu.getContent().stream()
                .map(GroupListDTO::fromMenuGroup)
                .collect(Collectors.toList());
        return ApiResponse.success(JSONObject.of("groups", groupList));
    }

    @Override
    public ApiResponse getGroupById(Long menuId, String groupId) throws NotFoundException {
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu))
            throw new IllegalArgumentException("menuId错误");
        MenuGroup menuGroup=menu.getMenuGroup(groupId);
        if (Objects.isNull(menuGroup))
            throw new NotFoundException("分组不存在");
        return ApiResponse.success(JSONObject.of("group",menuGroup));
    }

    @Override
    public ApiResponse getGroupBase(Long menuId, String groupId) throws NotFoundException {
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu))
            throw new IllegalArgumentException("menuId错误");
        MenuGroup group=menu.getMenuGroup(groupId);
        if (Objects.isNull(group))
            throw new NotFoundException("分组不存在");
        return ApiResponse.success(JSONObject.of("group", GroupBaseDTO.fromMenuGroup(group)));
    }

    @Override
    public ApiResponse updateGroupBase(Long menuId, String groupId, GroupUpdateBaseDTO data) throws NotFoundException {
        // 获取菜单
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu))
            throw new IllegalArgumentException("menuId错误");
        // 获取分组
        MenuGroup group=menu.getMenuGroup(groupId);
        if (Objects.isNull(group))
            throw new NotFoundException("分组不存在");
        // 修改分组数据
        group.setName(data.getName());
        group.setSort(data.getSort());
        // 分组列表排序
        menu.sortContent();
        // 更新menu
        menuMapper.updateById(menu);
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse deleteGroup(Long menuId, String groupId) {
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu))
            throw new IllegalArgumentException("menuId错误");
        menu.getContent().removeIf(item->item.getId().equals(groupId));
        menuMapper.updateById(menu);
        return ApiResponse.success("删除成功");
    }
}

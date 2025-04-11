package cn.dextea.menu.service.impl;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.menu.code.MenuErrorCode;
import cn.dextea.menu.pojo.Menu;
import cn.dextea.menu.model.group.GroupBaseModel;
import cn.dextea.menu.model.group.GroupCreateRequest;
import cn.dextea.menu.model.group.GroupListModel;
import cn.dextea.menu.model.group.GroupUpdateBaseRequest;
import cn.dextea.menu.mapper.MenuMapper;
import cn.dextea.menu.pojo.MenuGroup;
import cn.dextea.menu.service.GroupService;
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
    public DexteaApiResponse<Void> createGroup(Long menuId, GroupCreateRequest data) {
        // 读取menu
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu)) {
            return DexteaApiResponse.fail(MenuErrorCode.MENU_ID_ILLEGAL.getCode(),
                    MenuErrorCode.MENU_ID_ILLEGAL.getMsg());
        }
        // 创建分组
        MenuGroup menuGroup = data.toMenuGroup();
        menu.getContent().add(menuGroup);
        // 排序
        menu.sortContent();
        // 更新menu
        menuMapper.updateById(menu);
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<List<GroupListModel>> getGroupList(Long menuId) {
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu)) {
            return DexteaApiResponse.fail(MenuErrorCode.MENU_ID_ILLEGAL.getCode(),
                    MenuErrorCode.MENU_ID_ILLEGAL.getMsg());
        }
        List<GroupListModel> groupList = menu.getContent().stream()
                .map(GroupListModel::fromMenuGroup)
                .collect(Collectors.toList());
        return DexteaApiResponse.success(groupList);
    }

    @Override
    public DexteaApiResponse<MenuGroup> getGroupById(Long menuId, String groupId) {
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu)) {
            return DexteaApiResponse.fail(MenuErrorCode.MENU_ID_ILLEGAL.getCode(),
                    MenuErrorCode.MENU_ID_ILLEGAL.getMsg());
        }
        MenuGroup menuGroup=menu.getMenuGroup(groupId);
        if (Objects.isNull(menuGroup)) {
            return DexteaApiResponse.notFound(MenuErrorCode.GROUP_NOT_FOUND.getCode(),
                    MenuErrorCode.GROUP_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success(menuGroup);
    }

    @Override
    public DexteaApiResponse<GroupBaseModel> getGroupBase(Long menuId, String groupId) {
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu)) {
            return DexteaApiResponse.fail(MenuErrorCode.MENU_ID_ILLEGAL.getCode(),
                    MenuErrorCode.MENU_ID_ILLEGAL.getMsg());
        }
        MenuGroup group=menu.getMenuGroup(groupId);
        if (Objects.isNull(group)) {
            return DexteaApiResponse.notFound(MenuErrorCode.GROUP_NOT_FOUND.getCode(),
                    MenuErrorCode.GROUP_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success(GroupBaseModel.fromMenuGroup(group));
    }

    @Override
    public DexteaApiResponse<Void> updateGroupBase(Long menuId, String groupId, GroupUpdateBaseRequest data)  {
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu)) {
            return DexteaApiResponse.fail(MenuErrorCode.MENU_ID_ILLEGAL.getCode(),
                    MenuErrorCode.MENU_ID_ILLEGAL.getMsg());
        }
        // 获取分组
        MenuGroup group=menu.getMenuGroup(groupId);
        if (Objects.isNull(group)) {
            return DexteaApiResponse.notFound(MenuErrorCode.GROUP_NOT_FOUND.getCode(),
                MenuErrorCode.GROUP_NOT_FOUND.getMsg());
        }
        // 修改分组数据
        group.setName(data.getName());
        group.setSort(data.getSort());
        // 分组列表排序
        menu.sortContent();
        // 更新menu
        menuMapper.updateById(menu);
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<Void> deleteGroup(Long menuId, String groupId) {
        Menu menu=menuMapper.selectById(menuId);
        if (Objects.isNull(menu)) {
            return DexteaApiResponse.fail(MenuErrorCode.MENU_ID_ILLEGAL.getCode(),
                    MenuErrorCode.MENU_ID_ILLEGAL.getMsg());
        }
        menu.getContent().removeIf(item->item.getId().equals(groupId));
        menuMapper.updateById(menu);
        return DexteaApiResponse.success();
    }
}

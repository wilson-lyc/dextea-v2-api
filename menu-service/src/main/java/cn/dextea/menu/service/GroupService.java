package cn.dextea.menu.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.menu.MenuGroupModel;
import cn.dextea.menu.model.group.GroupCreateRequest;
import cn.dextea.menu.model.group.GroupUpdateBaseRequest;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface GroupService {
    DexteaApiResponse<Void> createGroup(Long menuId, GroupCreateRequest data);
    DexteaApiResponse<List<MenuGroupModel>> getGroupList(Long menuId);
    DexteaApiResponse<MenuGroupModel> getGroupBase(Long menuId, String groupId);
    DexteaApiResponse<Void> updateGroupBase(Long menuId, String groupId, GroupUpdateBaseRequest data);
    DexteaApiResponse<Void> deleteGroup(Long menuId, String groupId);
}

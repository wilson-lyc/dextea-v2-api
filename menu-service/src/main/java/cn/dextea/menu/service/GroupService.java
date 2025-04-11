package cn.dextea.menu.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.menu.model.group.GroupBaseModel;
import cn.dextea.menu.model.group.GroupCreateRequest;
import cn.dextea.menu.model.group.GroupListModel;
import cn.dextea.menu.model.group.GroupUpdateBaseRequest;
import cn.dextea.menu.pojo.MenuGroup;
import org.apache.ibatis.javassist.NotFoundException;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface GroupService {
    DexteaApiResponse<Void> createGroup(Long menuId, GroupCreateRequest data);
    DexteaApiResponse<List<GroupListModel>> getGroupList(Long menuId);
    DexteaApiResponse<MenuGroup> getGroupById(Long menuId, String groupId);
    DexteaApiResponse<GroupBaseModel> getGroupBase(Long menuId, String groupId);
    DexteaApiResponse<Void> updateGroupBase(Long menuId, String groupId, GroupUpdateBaseRequest data);
    DexteaApiResponse<Void> deleteGroup(Long menuId, String groupId);
}

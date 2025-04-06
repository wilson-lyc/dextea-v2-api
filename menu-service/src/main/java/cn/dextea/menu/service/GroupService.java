package cn.dextea.menu.service;

import cn.dextea.common.model.common.ApiResponse;
import cn.dextea.menu.dto.group.GroupCreateDTO;
import cn.dextea.menu.dto.group.GroupUpdateBaseDTO;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface GroupService {
    ApiResponse createGroup(Long menuId,GroupCreateDTO data);
    ApiResponse getGroupList(Long menuId);
    ApiResponse getGroupById(Long menuId, String groupId) throws NotFoundException;
    ApiResponse getGroupBase(Long menuId, String groupId) throws NotFoundException;
    ApiResponse updateGroupBase(Long menuId, String groupId, GroupUpdateBaseDTO data) throws NotFoundException;
    ApiResponse deleteGroup(Long menuId, String groupId);
}

package cn.dextea.menu.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.menu.dto.GroupEditDTO;

/**
 * @author Lai Yongchao
 */
public interface GroupService {
    ApiResponse createGroup(Long menuId, GroupEditDTO data);
    ApiResponse getGroupList(Long menuId);
    ApiResponse getGroupInfo(Long groupId);
    ApiResponse updateGroupInfo(Long groupId, GroupEditDTO data);
}

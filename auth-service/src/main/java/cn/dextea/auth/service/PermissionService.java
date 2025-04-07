package cn.dextea.auth.service;

import cn.dextea.common.model.auth.PermissionModel;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.common.SelectOptionModel;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface PermissionService {
    DexteaApiResponse<List<PermissionModel>> getPermissionList();
}

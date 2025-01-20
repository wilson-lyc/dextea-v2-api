package cn.dextea.auth.service;

import cn.dextea.common.dto.ApiResponse;

/**
 * @author Lai Yongchao
 */
public interface RouterService {
    ApiResponse getRouterList();
    ApiResponse getStaffRouter(Long uid);
}

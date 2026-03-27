package cn.dextea.staff.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.dto.request.PermissionPageQueryRequest;
import cn.dextea.staff.dto.response.PermissionDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 权限管理服务
 */
public interface PermissionAdminService {

    /**
     * 分页查询权限列表
     *
     * @param request 分页及筛选条件
     * @return 返回权限分页结果集
     */
    ApiResponse<IPage<PermissionDetailResponse>> getPermissionPage(PermissionPageQueryRequest request);

    /**
     * 查询权限详情
     *
     * @param id 权限ID
     * @return 返回指定权限的详细信息
     */
    ApiResponse<PermissionDetailResponse> getPermissionDetail(Long id);
}

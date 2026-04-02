package cn.dextea.staff.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.dto.request.BindRolePermissionRequest;
import cn.dextea.staff.dto.request.CreateRoleRequest;
import cn.dextea.staff.dto.request.RolePageQueryRequest;
import cn.dextea.staff.dto.request.UpdateRoleRequest;
import cn.dextea.staff.dto.response.CreateRoleResponse;
import cn.dextea.staff.dto.response.RoleDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 角色管理服务
 */
public interface RoleAdminService {

    /**
     * 创建角色
     *
     * @param request 角色创建请求，包含角色名称、编码、描述和数据权限范围
     * @return 返回新建角色信息
     */
    ApiResponse<CreateRoleResponse> create(CreateRoleRequest request);

    /**
     * 分页查询角色列表
     *
     * @param request 分页及筛选条件，支持角色名称和状态过滤
     * @return 返回角色分页结果集
     */
    ApiResponse<IPage<RoleDetailResponse>> page(RolePageQueryRequest request);

    /**
     * 查询角色详情
     *
     * @param id 角色ID
     * @return 返回指定角色的详细信息
     */
    ApiResponse<RoleDetailResponse> detail(Long id);

    /**
     * 更新角色信息
     *
     * @param id 角色ID
     * @param request 角色更新请求，包含角色名称、编码、描述、状态和数据权限范围
     * @return 返回更新后的角色详情
     */
    ApiResponse<RoleDetailResponse> update(Long id, UpdateRoleRequest request);

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 返回删除结果
     */
    ApiResponse<Void> delete(Long id);

    /**
     * 为角色绑定权限
     *
     * @param id 角色ID
     * @param request 权限绑定请求，包含权限名称列表
     * @return 返回绑定结果
     */
    ApiResponse<Void> bindPermission(Long id, BindRolePermissionRequest request);

    /**
     * 解除角色的权限
     *
     * @param id 角色ID
     * @param permissionName 权限名称
     * @return 返回解除结果
     */
    ApiResponse<Void> unbindPermission(Long id, String permissionName);
}

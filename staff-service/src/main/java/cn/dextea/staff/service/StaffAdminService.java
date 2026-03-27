package cn.dextea.staff.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.dto.request.AssignStaffRoleRequest;
import cn.dextea.staff.dto.request.CreateStaffRequest;
import cn.dextea.staff.dto.request.StaffPageQueryRequest;
import cn.dextea.staff.dto.request.UpdateStaffRequest;
import cn.dextea.staff.dto.response.CreateStaffResponse;
import cn.dextea.staff.dto.response.ResetStaffPasswordResponse;
import cn.dextea.staff.dto.response.StaffDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 员工后台管理服务
 */
public interface StaffAdminService {

    /**
     * 创建员工账号
     *
     * @param request 员工创建请求，包含用户名、姓名和员工类型
     * @return 返回新建员工信息及系统生成的初始密码
     */
    ApiResponse<CreateStaffResponse> createStaff(CreateStaffRequest request);

    /**
     * 分页查询员工列表
     *
     * @param request 分页及筛选条件，支持用户名、姓名、类型和状态过滤
     * @return 返回员工分页结果集
     */
    ApiResponse<IPage<StaffDetailResponse>> getStaffPage(StaffPageQueryRequest request);

    /**
     * 查询员工详情
     *
     * @param id 员工ID
     * @return 返回指定员工的详细资料
     */
    ApiResponse<StaffDetailResponse> getStaffDetail(Long id);

    /**
     * 更新员工资料
     *
     * @param id 员工ID
     * @param request 员工更新请求，包含用户名、姓名、类型和账号状态
     * @return 返回更新后的员工详情
     */
    ApiResponse<StaffDetailResponse> updateStaff(Long id, UpdateStaffRequest request);

    /**
     * 禁用员工账号
     *
     * @param id 员工ID
     * @return 返回禁用处理结果
     */
    ApiResponse<Void> deleteStaff(Long id);

    /**
     * 重置员工登录密码
     *
     * @param id 员工ID
     * @return 返回系统生成的新密码
     */
    ApiResponse<ResetStaffPasswordResponse> resetPassword(Long id);

    /**
     * 为员工分配角色
     *
     * @param id 员工ID
     * @param request 角色分配请求，包含角色ID列表
     * @return 返回分配结果
     */
    ApiResponse<Void> assignRole(Long id, AssignStaffRoleRequest request);

    /**
     * 移除员工的角色
     *
     * @param id 员工ID
     * @param roleId 角色ID
     * @return 返回移除结果
     */
    ApiResponse<Void> unbindRole(Long id, Long roleId);
}

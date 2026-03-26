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
 * 员工后台管理服务。
 */
public interface StaffAdminService {
    ApiResponse<CreateStaffResponse> createStaff(CreateStaffRequest request);

    ApiResponse<IPage<StaffDetailResponse>> getStaffPage(StaffPageQueryRequest request);

    ApiResponse<StaffDetailResponse> getStaffDetail(Long id);

    ApiResponse<StaffDetailResponse> updateStaff(Long id, UpdateStaffRequest request);

    ApiResponse<Void> deleteStaff(Long id);

    ApiResponse<ResetStaffPasswordResponse> resetPassword(Long id);

    ApiResponse<Void> assignRole(Long id, AssignStaffRoleRequest request);

    ApiResponse<Void> unbindRole(Long id, Long roleId);
}

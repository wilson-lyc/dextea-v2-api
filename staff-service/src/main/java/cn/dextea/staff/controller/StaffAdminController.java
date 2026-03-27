package cn.dextea.staff.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.dto.request.AssignStaffRoleRequest;
import cn.dextea.staff.dto.request.CreateStaffRequest;
import cn.dextea.staff.dto.request.StaffPageQueryRequest;
import cn.dextea.staff.dto.request.UpdateStaffRequest;
import cn.dextea.staff.dto.response.CreateStaffResponse;
import cn.dextea.staff.dto.response.ResetStaffPasswordResponse;
import cn.dextea.staff.dto.response.StaffDetailResponse;
import cn.dextea.staff.service.StaffAdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/v1/admin/staffs")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class StaffAdminController {
    private final StaffAdminService staffAdminService;

    /**
     * 创建员工
     * @param request 创建员工请求参数
     * @return 创建结果
     */
    @PostMapping
    public ApiResponse<CreateStaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return staffAdminService.createStaff(request);
    }

    /**
     * 分页查询员工列表
     * @param request 员工分页查询请求参数
     * @return 员工分页数据
     */
    @GetMapping
    public ApiResponse<IPage<StaffDetailResponse>> getStaffPage(@Valid StaffPageQueryRequest request) {
        return staffAdminService.getStaffPage(request);
    }

    /**
     * 查询员工详情
     * @param id 员工ID
     * @return 员工详情
     */
    @GetMapping("/{id}")
    public ApiResponse<StaffDetailResponse> getStaffDetail(
            @PathVariable @Min(value = 1, message = "员工ID不能为空") Long id) {
        return staffAdminService.getStaffDetail(id);
    }

    /**
     * 更新员工资料
     * @param id 员工ID
     * @param request 更新员工请求参数
     * @return 更新后的员工详情
     */
    @PutMapping("/{id}")
    public ApiResponse<StaffDetailResponse> updateStaff(
            @PathVariable @Min(value = 1, message = "员工ID不能为空") Long id,
            @Valid @RequestBody UpdateStaffRequest request) {
        return staffAdminService.updateStaff(id, request);
    }

    /**
     * 禁用员工账号
     * @param id 员工ID
     * @return 禁用结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStaff(
            @PathVariable @Min(value = 1, message = "员工ID不能为空") Long id) {
        return staffAdminService.deleteStaff(id);
    }

    /**
     * 重置员工登录密码
     * @param id 员工ID
     * @return 重置后的密码信息
     */
    @PutMapping("/{id}/password/reset")
    public ApiResponse<ResetStaffPasswordResponse> resetPassword(
            @PathVariable @Min(value = 1, message = "员工ID不能为空") Long id) {
        return staffAdminService.resetPassword(id);
    }

    /**
     * 为员工分配角色
     * @param id 员工ID
     * @param request 员工分配角色请求参数
     * @return 分配结果
     */
    @PostMapping("/{id}/roles")
    public ApiResponse<Void> assignRole(
            @PathVariable @Min(value = 1, message = "员工ID不能为空") Long id,
            @Valid @RequestBody AssignStaffRoleRequest request) {
        return staffAdminService.assignRole(id, request);
    }

    /**
     * 移除员工的角色
     * @param id 员工ID
     * @param roleId 角色ID
     * @return 移除结果
     */
    @DeleteMapping("/{id}/roles/{roleId}")
    public ApiResponse<Void> unbindRole(
            @PathVariable @Min(value = 1, message = "员工ID不能为空") Long id,
            @PathVariable @Min(value = 1, message = "角色ID不能为空") Long roleId) {
        return staffAdminService.unbindRole(id, roleId);
    }
}

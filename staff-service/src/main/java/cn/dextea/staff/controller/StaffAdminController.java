package cn.dextea.staff.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
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

@RestController
@RequestMapping("/v1/admin/staffs")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class StaffAdminController {
    private final StaffAdminService staffAdminService;

    /**
     * 创建员工账号
     *
     * @param request 员工创建请求，包含用户名、姓名和员工类型
     * @return 返回新建员工信息及系统生成的初始密码
     */
    @PostMapping
    public ApiResponse<CreateStaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return staffAdminService.createStaff(request);
    }

    /**
     * 分页查询员工列表
     *
     * @param request 分页及筛选条件，支持用户名、姓名、类型和状态过滤
     * @return 返回员工分页结果集
     */
    @GetMapping
    public ApiResponse<IPage<StaffDetailResponse>> getStaffPage(@Valid StaffPageQueryRequest request) {
        return staffAdminService.getStaffPage(request);
    }

    /**
     * 查询员工详情
     *
     * @param id 员工 ID
     * @return 返回指定员工的详细资料
     */
    @GetMapping("/{id}")
    public ApiResponse<StaffDetailResponse> getStaffDetail(@PathVariable @Min(value = 1, message = "员工ID不能为空") Long id) {
        return staffAdminService.getStaffDetail(id);
    }

    /**
     * 更新员工资料
     *
     * @param id 员工 ID
     * @param request 员工更新请求，包含用户名、姓名、类型和账号状态
     * @return 返回更新后的员工详情
     */
    @PutMapping("/{id}")
    public ApiResponse<StaffDetailResponse> updateStaff(
            @PathVariable @Min(value = 1, message = "员工ID不能为空") Long id,
            @Valid @RequestBody UpdateStaffRequest request) {
        return staffAdminService.updateStaff(id, request);
    }

    /**
     * 禁用员工账号
     *
     * @param id 员工 ID
     * @return 返回禁用处理结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStaff(@PathVariable @Min(value = 1, message = "员工ID不能为空") Long id) {
        return staffAdminService.deleteStaff(id);
    }

    /**
     * 重置员工登录密码
     *
     * @param id 员工 ID
     * @return 返回系统生成的新密码
     */
    @PutMapping("/{id}/password/reset")
    public ApiResponse<ResetStaffPasswordResponse> resetPassword(
            @PathVariable @Min(value = 1, message = "员工ID不能为空") Long id) {
        return staffAdminService.resetPassword(id);
    }
}

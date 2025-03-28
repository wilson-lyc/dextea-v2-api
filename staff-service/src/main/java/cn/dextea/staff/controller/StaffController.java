package cn.dextea.staff.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.staff.dto.StaffLoginRequest;
import cn.dextea.staff.dto.*;
import cn.dextea.staff.service.StaffService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class StaffController {
    @Resource
    private StaffService staffService;

    /**
     * 创建员工
     * @param data 数据
     */
    @PostMapping("/staff")
    public DexteaApiResponse<StaffCreateResponse> createStaff(
            @Valid @RequestBody StaffCreateRequest data) {
        return staffService.createStaff(data);
    }

    /**
     * 获取员工列表
     * @param current 当前页码
     * @param size 每页大小
     * @param filter 搜索条件
     */
    @GetMapping("/staff")
    public DexteaApiResponse<IPage<StaffListResponse>> getStaffList(
            @Valid @Min(value = 1,message = "current不能小于1") @RequestParam(defaultValue = "1") int current,
            @Valid @Min(value = 1,message = "size不能小于1") @RequestParam(defaultValue = "10") int size,
            @Valid StaffFilter filter){
        return staffService.getStaffList(current,size,filter);
    }

    /**
     * 获取员工详情
     * @param id 员工id
     * @return 员工信息
     */
    @GetMapping("/staff/{id:\\d+}")
    public DexteaApiResponse<StaffInfoResponse> getStaffInfo(@PathVariable("id") Long id) throws NotFoundException {
        return staffService.getStaffInfo(id);
    }

    @GetMapping("/staff/{id:\\d+}/status")
    public DexteaApiResponse<StaffStatusResponse> getStaffStatus(@PathVariable("id") Long id) throws NotFoundException {
        return staffService.getStaffStatus(id);
    }

    /**
     * 更新员工详情
     * @param id 员工id
     * @param data 数据
     */
    @PutMapping("/staff/{id:\\d+}")
    public DexteaApiResponse<StaffInfoResponse> updateStaffInfo(
            @PathVariable("id") Long id,
            @Valid @RequestBody StaffUpdateRequest data) throws NotFoundException {
        return staffService.updateStaffInfo(id,data);
    }

    @PutMapping("/staff/{id:\\d+}/status")
    public DexteaApiResponse<StaffInfoResponse> updateStaffStatus(
            @PathVariable Long id,
            @RequestParam Integer status) throws NotFoundException {
        return staffService.updateStaffStatus(id,status);
    }

    /**
     * 系统重置密码
     * @param id 员工id
     */
    @PutMapping("/staff/{id:\\d+}/password-auto")
    public DexteaApiResponse<StaffResetPasswordResponse> sysResetPwd(@PathVariable("id") Long id) throws NotFoundException {
        return staffService.sysResetPwd(id);
    }

    /**
     * 修改密码
     * @param id 员工id
     * @param data 数据
     */
    @PutMapping("/staff/{id:\\d+}/password")
    public DexteaApiResponse<StaffInfoResponse> updateStaffPwd(
            @PathVariable("id") Long id,
            @Valid @RequestBody StaffUpdatePwdRequest data) throws NotFoundException {
        return staffService.updateStaffPwd(id,data);
    }

    /**
     * 登录
     * @param data 数据
     */
    @PostMapping("/staff/login")
    public DexteaApiResponse<StaffLoginResponse> staffLogin(@Valid @RequestBody StaffLoginRequest data) throws IllegalAccessException {
        return staffService.staffLogin(data);
    }
}

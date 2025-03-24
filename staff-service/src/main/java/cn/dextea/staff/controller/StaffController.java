package cn.dextea.staff.controller;

import cn.dextea.common.code.StaffStatus;
import cn.dextea.common.dto.ApiResponse;
import cn.dextea.staff.dto.StaffLoginDTO;
import cn.dextea.staff.dto.*;
import cn.dextea.staff.service.StaffService;
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
    public ApiResponse createStaff(@Valid @RequestBody StaffCreateDTO data) throws NotFoundException {
        return staffService.createStaff(data);
    }

    /**
     * 获取员工列表
     * @param current 当前页
     * @param size 每页大小
     * @param filter 搜索条件
     * @return 员工列表
     */
    @GetMapping("/staff")
    public ApiResponse getStaffList(
            @Valid @Min(value = 1,message = "current不能小于1") @RequestParam(defaultValue = "1") int current,
            @Valid @Min(value = 1,message = "size不能小于1") @RequestParam(defaultValue = "10") int size,
            @Valid StaffQueryDTO filter){
        return staffService.getStaffList(current,size,filter);
    }

    /**
     * 获取员工详情
     * @param id 员工id
     * @return 员工信息
     */
    @GetMapping("/staff/{id:\\d+}")
    public ApiResponse getStaffInfo(@Valid @PathVariable("id") Long id) throws NotFoundException {
        return staffService.getStaffInfo(id);
    }

    @GetMapping("/staff/{id:\\d+}/status")
    public ApiResponse getStaffStatus(@PathVariable("id") Long id) throws NotFoundException {
        return staffService.getStaffStatus(id);
    }

    /**
     * 更新员工详情
     * @param id 员工id
     * @param data 数据
     */
    @PutMapping("/staff/{id:\\d+}")
    public ApiResponse updateStaffInfo(
            @PathVariable("id") Long id,
            @Valid @RequestBody StaffUpdateDTO data) throws NotFoundException {
        return staffService.updateStaffInfo(id,data);
    }

    @PutMapping("/staff/{id:\\d+}/status")
    public ApiResponse updateStaffStatus(
            @PathVariable Long id,
            @RequestParam Integer status) throws NotFoundException {
        return staffService.updateStaffStatus(id,StaffStatus.fromValue(status));
    }

    /**
     * 系统重置密码
     * @param id 员工id
     */
    @PutMapping("/staff/{id:\\d+}/password-auto")
    public ApiResponse sysResetPwd(@PathVariable("id") Long id) throws NotFoundException {
        return staffService.sysResetPwd(id);
    }

    /**
     * 修改密码
     * @param id 员工id
     * @param data 数据
     */
    @PutMapping("/staff/{id:\\d+}/password")
    public ApiResponse updateStaffPwd(
            @PathVariable("id") Long id,
            @Valid @RequestBody StaffUpdatePwdDTO data) throws NotFoundException {
        return staffService.updateStaffPwd(id,data);
    }

    /**
     * 登录
     * @param data 数据
     */
    @PostMapping("/staff/login")
    public ApiResponse login(@Valid @RequestBody StaffLoginDTO data) throws IllegalAccessException {
        return staffService.login(data);
    }
}

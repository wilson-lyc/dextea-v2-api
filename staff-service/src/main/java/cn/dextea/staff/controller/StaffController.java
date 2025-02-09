package cn.dextea.staff.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.staff.dto.StaffLoginDTO;
import cn.dextea.staff.dto.*;
import cn.dextea.staff.service.StaffService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class StaffController {
    @Autowired
    private StaffService staffService;

    /**
     * 创建员工
     * @param data {name,phone,side,storeId}
     * @return 账号和密码
     */
    @PostMapping("/staff")
    public ApiResponse create(@Valid @RequestBody CreateStaffDTO data) {
        return staffService.create(data);
    }

    /**
     * 根据id查询员工
     * @param id 员工id
     * @return 员工信息
     */
    @GetMapping("/staff/{id:\\d+}")
    public ApiResponse getStaffById(@Valid @PathVariable("id") Long id){
        return staffService.getStaffById(id);
    }

    /**
     * 查询员工列表
     * @param current 当前页
     * @param size 每页大小
     * @param filter {name,phone,role,storeId,status,side}
     * @return 员工列表
     */
    @PostMapping("/staff/search")
    public ApiResponse getStaffList(
            @Valid @Min(value = 1,message = "current不能小于1") @RequestParam(defaultValue = "1") int current,
            @Valid @Min(value = 1,message = "size不能小于1") @RequestParam(defaultValue = "10") int size,
            @Valid @RequestBody SearchStaffDTO filter){
        return staffService.getStaffList(current,size,filter);
    }

    /**
     * 重置密码 - 系统自动生成新密码
     * @param id 员工id
     * @return 新密码
     */
    @PutMapping("/staff/resetPwd")
    public ApiResponse resetPwd(@RequestParam("id") Long id){
        return staffService.resetPwd(id);
    }

    /**
     * 更新员工信息
     * @param id 员工id
     * @param data {phone,role,storeId,state}
     */
    @PutMapping("/staff/{id:\\d+}")
    public ApiResponse updateStaff(@PathVariable("id") Long id,@Valid @RequestBody UpdateStaffDTO data){
        return staffService.update(id,data);
    }
    /**
     * 登录
     * @param data {account,password}
     */
    @PostMapping("/staff/login")
    public ApiResponse login(@Valid @RequestBody StaffLoginDTO data){
        return staffService.login(data);
    }

    /**
     * 激活员工
     * @param id 员工id
     */
    @PutMapping("/staff/active")
    public ApiResponse active(@RequestParam("id") Long id){
        return staffService.active(id);
    }

    /**
     * 禁用员工
     * @param id 员工id
     */
    @DeleteMapping("/staff/ban")
    public ApiResponse ban(@RequestParam("id") Long id){
        return staffService.ban(id);
    }

    /**
     * 修改密码
     * @param id 员工id
     * @param data {oldPwd,newPwd}
     */
    @PutMapping("/staff/{id:\\d+}/password")
    public ApiResponse updatePwd(@PathVariable("id") Long id,@Valid @RequestBody UpdatePwdDTO data){
        return staffService.updatePwd(id,data);
    }
}

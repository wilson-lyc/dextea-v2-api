package cn.dextea.staff.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.staff.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lai Yongchao
 */
@RestController
public class StaffController {
    @Autowired
    private StaffService staffService;

    /**
     * 根据id查询员工信息
     * @param id 员工id
     * @return 员工信息
     */
    @GetMapping("/staff/info/{id}")
    public ApiResponse getStaffById(@Valid @PathVariable("id") int id){
        return staffService.getStaffById(id);
    }

    /**
     * 查询员工列表
     * @param current 当前页码 默认=1
     * @param size 分页大小 默认=10
     * @param id 员工ID 可选
     * @param role 角色 可选
     * @param phone 手机号 可选
     * @param state 状态 可选
     * @return 员工列表
     */
    @GetMapping("/staff/list")
    public ApiResponse getStaffList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "-1") int id,
            @RequestParam(defaultValue = "") String role,
            @RequestParam(defaultValue = "") String phone,
            @RequestParam(defaultValue = "-1")int state){
        return staffService.getStaffList(current,size,id,role,phone,state);
    }

    @GetMapping("/staff/resetPwd")
    public ApiResponse resetPwd(@RequestParam("id") int id){
        return staffService.resetPwd(id);
    }
}

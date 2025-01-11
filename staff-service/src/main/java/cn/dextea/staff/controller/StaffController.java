package cn.dextea.staff.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.staff.dto.RegisterDTO;
import cn.dextea.staff.dto.SearchStaffDTO;
import cn.dextea.staff.dto.UpdateStaffDTO;
import cn.dextea.staff.service.StaffService;
import jakarta.validation.Valid;
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
     * @param data {name,phone,role}
     * @return 账号和密码
     */
    @PostMapping("/staff")
    public ApiResponse create(@Valid @RequestBody RegisterDTO data) {
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
     * @param data {current,size,name,account,phone,role,storeId,state}
     * @return 员工列表
     */
    @PostMapping("/staff/search")
    public ApiResponse getStaffList(@Valid @RequestBody SearchStaffDTO data){
        return staffService.search(data);
    }

    /**
     * 重置密码
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
     * @return
     */
    @PutMapping("/staff/{id:\\d+}")
    public ApiResponse updateStaff(@PathVariable("id") Long id,@Valid @RequestBody UpdateStaffDTO data){
        return staffService.update(id,data);
    }
}

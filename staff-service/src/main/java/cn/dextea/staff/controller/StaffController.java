package cn.dextea.staff.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.ResponseDTO;
import cn.dextea.staff.dto.GetStaffListDTO;
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
    @GetMapping("/staff/{id}")
    public ApiResponse getStaffById(@Valid @PathVariable("id") int id){
        return staffService.getStaffById(id);
    }
    @GetMapping("/staff/list")
    public ApiResponse getStaffList(@RequestParam int currentPage,@RequestParam int pageSize){
        return staffService.getStaffList(currentPage,pageSize);
    }
}

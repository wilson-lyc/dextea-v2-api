package cn.dextea.staff.controller;

import cn.dextea.common.dto.ResponseDTO;
import cn.dextea.staff.dto.RegisterDTO;
import cn.dextea.staff.service.StaffInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 员工
 * @author Lai Yongchao
 */
@RestController
public class RegisterController {
    @Autowired
    StaffInfoService staffInfoService;

    @PostMapping("/staff")
    public ResponseDTO register(@Valid @RequestBody RegisterDTO data) {
        return staffInfoService.register(data.getName(),data.getRole(),data.getPhone());
    }
}

package cn.dextea.staff.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.ResponseDTO;
import cn.dextea.staff.dto.RegisterDTO;
import cn.dextea.staff.service.RegisterService;
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
    private RegisterService registerService;

    @PostMapping("/staff")
    public ApiResponse register(@Valid @RequestBody RegisterDTO data) {
        return registerService.register(data.getName(),data.getRole(),data.getPhone());
    }
}

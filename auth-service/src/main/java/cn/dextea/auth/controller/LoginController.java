package cn.dextea.auth.controller;

import cn.dextea.auth.dto.StaffLoginDTO;
import cn.dextea.auth.service.LoginService;
import cn.dextea.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lai Yongchao
 */
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;
    /**
     * 员工登录
     */
    @PostMapping("/login/staff")
    public ApiResponse staffLogin(@Valid @RequestBody StaffLoginDTO data){
        return loginService.staffLogin(data);
    }
}

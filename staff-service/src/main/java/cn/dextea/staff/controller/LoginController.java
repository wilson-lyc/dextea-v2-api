package cn.dextea.staff.controller;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.staff.StaffModel;
import cn.dextea.staff.model.StaffLoginRequest;
import cn.dextea.staff.service.LoginService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lai Yongchao
 */
@RestController
public class LoginController {
    @Resource
    private LoginService loginService;

    /**
     * 登录
     * @param data 数据
     */
    @PostMapping("/staff/login")
    public DexteaApiResponse<StaffModel> staffLogin(@Valid @RequestBody StaffLoginRequest data){
        return loginService.staffLogin(data);
    }
}

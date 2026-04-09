package cn.dextea.staff.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.dto.request.StaffLoginRequest;
import cn.dextea.staff.dto.request.StaffUpdatePasswordRequest;
import cn.dextea.staff.dto.response.StaffLoginResponse;
import cn.dextea.staff.service.StaffAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工认证
 */
@RestController
@RequiredArgsConstructor
public class StaffAuthController {
    private final StaffAuthService staffAuthService;

    /**
     * 员工账号登录
     * @param request 登录请求参数
     * @param httpServletRequest HTTP请求对象
     * @return 登录结果
     */
    @PostMapping("/v1/staff/login")
    public ApiResponse<StaffLoginResponse> login(
            @Valid @RequestBody StaffLoginRequest request,
            HttpServletRequest httpServletRequest) {
        return staffAuthService.login(request, httpServletRequest);
    }

    /**
     * 员工修改登录密码
     * @param request 修改密码请求参数
     * @return 修改结果
     */
    @PutMapping("/v1/staff/password")
    @SaCheckLogin
    public ApiResponse<Void> updatePassword(@Valid @RequestBody StaffUpdatePasswordRequest request) {
        return staffAuthService.updatePassword(request);
    }
}

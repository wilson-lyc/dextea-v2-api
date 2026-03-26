package cn.dextea.staff.controller;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.dto.request.StaffLoginRequest;
import cn.dextea.staff.dto.response.StaffLoginResponse;
import cn.dextea.staff.service.StaffAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工认证控制器。
 *
 * <p>负责员工登录认证相关接口，登录态与访问令牌由 Sa-Token 统一托管。</p>
 */
@RestController
@RequiredArgsConstructor
public class StaffAuthController {
    private final StaffAuthService staffAuthService;

    /**
     * 员工账号登录。
     *
     * @param request 登录请求，包含用户名和密码
     * @param httpServletRequest HTTP 请求对象，用于提取客户端来源信息
     * @return 返回 Sa-Token 令牌信息及当前员工基础资料
     */
    @PostMapping("/v1/staff/login")
    public ApiResponse<StaffLoginResponse> login(
            @Valid @RequestBody StaffLoginRequest request,
            HttpServletRequest httpServletRequest) {
        return staffAuthService.login(request, httpServletRequest);
    }
}

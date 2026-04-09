package cn.dextea.customer.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.customer.dto.request.CustomerCodeLoginRequest;
import cn.dextea.customer.dto.request.CustomerLoginRequest;
import cn.dextea.customer.dto.request.RegisterCustomerRequest;
import cn.dextea.customer.dto.request.ResetCustomerPasswordRequest;
import cn.dextea.customer.dto.request.SendEmailCodeRequest;
import cn.dextea.customer.dto.request.UpdateCustomerPasswordRequest;
import cn.dextea.customer.dto.response.CustomerLoginResponse;
import cn.dextea.customer.dto.response.RegisterCustomerResponse;
import cn.dextea.customer.service.CustomerAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 顾客认证
 */
@RestController
@RequiredArgsConstructor
@Validated
public class CustomerAuthController {

    private final CustomerAuthService customerAuthService;

    /**
     * 发送邮箱验证码
     * @param request 发送验证码请求参数
     * @return 发送结果
     */
    @PostMapping("/v1/customer/email/code")
    public ApiResponse<Void> sendEmailCode(@Valid @RequestBody SendEmailCodeRequest request) {
        return customerAuthService.sendEmailCode(request);
    }

    /**
     * 邮箱注册（需验证码）
     * @param request 注册请求参数
     * @return 注册结果
     */
    @PostMapping("/v1/customer/register")
    public ApiResponse<RegisterCustomerResponse> register(@Valid @RequestBody RegisterCustomerRequest request) {
        return customerAuthService.register(request);
    }

    /**
     * 邮箱密码登录
     * @param request 登录请求参数
     * @return 登录结果
     */
    @PostMapping("/v1/customer/login")
    public ApiResponse<CustomerLoginResponse> login(@Valid @RequestBody CustomerLoginRequest request) {
        return customerAuthService.login(request);
    }

    /**
     * 邮箱验证码登录
     * @param request 验证码登录请求参数
     * @return 登录结果
     */
    @PostMapping("/v1/customer/login/code")
    public ApiResponse<CustomerLoginResponse> loginWithCode(@Valid @RequestBody CustomerCodeLoginRequest request) {
        return customerAuthService.loginWithCode(request);
    }

    /**
     * 退出登录
     * @return 退出结果
     */
    @PostMapping("/v1/customer/logout")
    @SaCheckLogin
    public ApiResponse<Void> logout() {
        return customerAuthService.logout();
    }

    /**
     * 修改密码（需登录，需原密码）
     * @param request 修改密码请求参数
     * @return 修改结果
     */
    @PutMapping("/v1/customer/password")
    @SaCheckLogin
    public ApiResponse<Void> updatePassword(@Valid @RequestBody UpdateCustomerPasswordRequest request) {
        return customerAuthService.updatePassword(request);
    }

    /**
     * 重置密码（无需登录，需验证码）
     * @param request 重置密码请求参数
     * @return 重置结果
     */
    @PostMapping("/v1/customer/password/reset")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetCustomerPasswordRequest request) {
        return customerAuthService.resetPassword(request);
    }
}

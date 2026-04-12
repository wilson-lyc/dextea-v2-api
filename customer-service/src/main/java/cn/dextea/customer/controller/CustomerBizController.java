package cn.dextea.customer.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.customer.dto.request.UpdateCustomerProfileRequest;
import cn.dextea.customer.dto.response.CustomerDetailResponse;
import cn.dextea.customer.service.CustomerBizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 顾客端个人资料
 */
@RestController
@RequestMapping("/v1/biz/customers")
@RequiredArgsConstructor
@Validated
@SaCheckLogin
public class CustomerBizController {

    private final CustomerBizService customerBizService;

    /**
     * 获取当前登录顾客资料
     * @return 顾客资料详情
     */
    @GetMapping("/me")
    public ApiResponse<CustomerDetailResponse> getProfile() {
        return customerBizService.getProfile();
    }

    /**
     * 更新当前登录顾客资料
     * @param request 更新资料请求参数
     * @return 更新后的顾客资料
     */
    @PutMapping("/me")
    public ApiResponse<CustomerDetailResponse> updateProfile(@Valid @RequestBody UpdateCustomerProfileRequest request) {
        return customerBizService.updateProfile(request);
    }
}

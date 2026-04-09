package cn.dextea.customer.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.customer.dto.request.CustomerCodeLoginRequest;
import cn.dextea.customer.dto.request.CustomerLoginRequest;
import cn.dextea.customer.dto.request.RegisterCustomerRequest;
import cn.dextea.customer.dto.request.ResetCustomerPasswordRequest;
import cn.dextea.customer.dto.request.SendEmailCodeRequest;
import cn.dextea.customer.dto.request.UpdateCustomerPasswordRequest;
import cn.dextea.customer.dto.response.CustomerLoginResponse;
import cn.dextea.customer.dto.response.RegisterCustomerResponse;

public interface CustomerAuthService {

    ApiResponse<Void> sendEmailCode(SendEmailCodeRequest request);

    ApiResponse<RegisterCustomerResponse> register(RegisterCustomerRequest request);

    ApiResponse<CustomerLoginResponse> login(CustomerLoginRequest request);

    ApiResponse<CustomerLoginResponse> loginWithCode(CustomerCodeLoginRequest request);

    ApiResponse<Void> logout();

    ApiResponse<Void> updatePassword(UpdateCustomerPasswordRequest request);

    ApiResponse<Void> resetPassword(ResetCustomerPasswordRequest request);
}

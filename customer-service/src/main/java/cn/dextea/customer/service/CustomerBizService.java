package cn.dextea.customer.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.customer.dto.request.UpdateCustomerProfileRequest;
import cn.dextea.customer.dto.response.CustomerDetailResponse;

public interface CustomerBizService {

    ApiResponse<CustomerDetailResponse> getProfile();

    ApiResponse<CustomerDetailResponse> updateProfile(UpdateCustomerProfileRequest request);
}

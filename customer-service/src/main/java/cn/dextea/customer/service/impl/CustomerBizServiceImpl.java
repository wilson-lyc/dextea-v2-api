package cn.dextea.customer.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.customer.converter.CustomerConverter;
import cn.dextea.customer.dto.request.UpdateCustomerProfileRequest;
import cn.dextea.customer.dto.response.CustomerDetailResponse;
import cn.dextea.customer.entity.CustomerEntity;
import cn.dextea.customer.enums.CustomerErrorCode;
import cn.dextea.customer.mapper.CustomerMapper;
import cn.dextea.customer.service.CustomerBizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 顾客端业务服务实现。
 */
@Service
@RequiredArgsConstructor
public class CustomerBizServiceImpl implements CustomerBizService {

    private final CustomerMapper customerMapper;
    private final CustomerConverter customerConverter;

    @Override
    public ApiResponse<CustomerDetailResponse> getProfile() {
        Long customerId = StpUtil.getLoginIdAsLong();
        CustomerEntity entity = customerMapper.selectById(customerId);
        if (entity == null) {
            return fail(CustomerErrorCode.CUSTOMER_NOT_FOUND);
        }
        return ApiResponse.success(customerConverter.toDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CustomerDetailResponse> updateProfile(UpdateCustomerProfileRequest request) {
        Long customerId = StpUtil.getLoginIdAsLong();
        CustomerEntity entity = customerMapper.selectById(customerId);
        if (entity == null) {
            return fail(CustomerErrorCode.CUSTOMER_NOT_FOUND);
        }

        entity.setNickname(request.getNickname().trim());

        if (customerMapper.updateById(entity) != 1) {
            return fail(CustomerErrorCode.UPDATE_PROFILE_FAILED);
        }

        return ApiResponse.success(customerConverter.toDetailResponse(entity));
    }

    private <T> ApiResponse<T> fail(CustomerErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}

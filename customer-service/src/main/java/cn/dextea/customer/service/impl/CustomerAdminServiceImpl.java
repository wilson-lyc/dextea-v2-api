package cn.dextea.customer.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.customer.converter.CustomerConverter;
import cn.dextea.customer.dto.request.CustomerPageQueryRequest;
import cn.dextea.customer.dto.response.CustomerDetailResponse;
import cn.dextea.customer.entity.CustomerEntity;
import cn.dextea.customer.enums.CustomerErrorCode;
import cn.dextea.customer.enums.CustomerStatus;
import cn.dextea.customer.mapper.CustomerMapper;
import cn.dextea.customer.service.CustomerAdminService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 顾客管理服务实现。
 */
@Service
@RequiredArgsConstructor
public class CustomerAdminServiceImpl implements CustomerAdminService {

    private final CustomerMapper customerMapper;
    private final CustomerConverter customerConverter;

    @Override
    public ApiResponse<IPage<CustomerDetailResponse>> page(CustomerPageQueryRequest request) {
        LambdaQueryWrapper<CustomerEntity> queryWrapper = new LambdaQueryWrapper<CustomerEntity>()
                .like(request.getNickname() != null && !request.getNickname().isBlank(),
                        CustomerEntity::getNickname, request.getNickname())
                .like(request.getEmail() != null && !request.getEmail().isBlank(),
                        CustomerEntity::getEmail, request.getEmail())
                .eq(request.getStatus() != null, CustomerEntity::getStatus, request.getStatus())
                .orderByDesc(CustomerEntity::getCreateTime);

        IPage<CustomerEntity> entityPage = customerMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), queryWrapper);
        IPage<CustomerDetailResponse> responsePage = entityPage.convert(customerConverter::toDetailResponse);
        return ApiResponse.success(responsePage);
    }

    @Override
    public ApiResponse<CustomerDetailResponse> detail(Long id) {
        CustomerEntity entity = customerMapper.selectById(id);
        if (entity == null) {
            return fail(CustomerErrorCode.CUSTOMER_NOT_FOUND);
        }
        return ApiResponse.success(customerConverter.toDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> enable(Long id) {
        CustomerEntity entity = customerMapper.selectById(id);
        if (entity == null) {
            return fail(CustomerErrorCode.CUSTOMER_NOT_FOUND);
        }

        entity.setStatus(CustomerStatus.AVAILABLE.getValue());
        if (customerMapper.updateById(entity) != 1) {
            return fail(CustomerErrorCode.ENABLE_FAILED);
        }

        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> disable(Long id) {
        CustomerEntity entity = customerMapper.selectById(id);
        if (entity == null) {
            return fail(CustomerErrorCode.CUSTOMER_NOT_FOUND);
        }

        entity.setStatus(CustomerStatus.DISABLED.getValue());
        if (customerMapper.updateById(entity) != 1) {
            return fail(CustomerErrorCode.DISABLE_FAILED);
        }

        return ApiResponse.success();
    }

    private <T> ApiResponse<T> fail(CustomerErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}

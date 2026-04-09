package cn.dextea.customer.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.customer.converter.CustomerConverter;
import cn.dextea.customer.dto.request.CustomerCodeLoginRequest;
import cn.dextea.customer.dto.request.CustomerLoginRequest;
import cn.dextea.customer.dto.request.RegisterCustomerRequest;
import cn.dextea.customer.dto.request.ResetCustomerPasswordRequest;
import cn.dextea.customer.dto.request.SendEmailCodeRequest;
import cn.dextea.customer.dto.request.UpdateCustomerPasswordRequest;
import cn.dextea.customer.dto.response.CustomerLoginResponse;
import cn.dextea.customer.dto.response.RegisterCustomerResponse;
import cn.dextea.customer.entity.CustomerEntity;
import cn.dextea.customer.enums.CustomerErrorCode;
import cn.dextea.customer.enums.CustomerStatus;
import cn.dextea.customer.enums.EmailCodeScene;
import cn.dextea.customer.mapper.CustomerMapper;
import cn.dextea.customer.service.CustomerAuthService;
import cn.dextea.customer.service.EmailCodeService;
import cn.dextea.customer.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 顾客认证服务实现。
 */
@Service
@RequiredArgsConstructor
public class CustomerAuthServiceImpl implements CustomerAuthService {

    private final CustomerMapper customerMapper;
    private final PasswordUtil passwordUtil;
    private final CustomerConverter customerConverter;
    private final EmailCodeService emailCodeService;

    @Override
    public ApiResponse<Void> sendEmailCode(SendEmailCodeRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        EmailCodeScene scene = EmailCodeScene.of(request.getScene());
        return emailCodeService.sendCode(email, scene);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<RegisterCustomerResponse> register(RegisterCustomerRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String nickname = request.getNickname().trim();

        // 先校验验证码，通过后验证码立即失效。
        if (!emailCodeService.verifyCode(email, EmailCodeScene.REGISTER, request.getCode())) {
            return fail(CustomerErrorCode.EMAIL_CODE_INVALID);
        }

        // 邮箱全局唯一，注册前做冲突校验。
        if (existsByEmail(email)) {
            return fail(CustomerErrorCode.EMAIL_ALREADY_EXISTS);
        }

        CustomerEntity entity = CustomerEntity.builder()
                .nickname(nickname)
                .email(email)
                .password(passwordUtil.encode(request.getPassword()))
                .status(CustomerStatus.AVAILABLE.getValue())
                .build();

        if (customerMapper.insert(entity) != 1) {
            return fail(CustomerErrorCode.REGISTER_FAILED);
        }

        return ApiResponse.success(customerConverter.toRegisterResponse(entity));
    }

    @Override
    public ApiResponse<CustomerLoginResponse> login(CustomerLoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        CustomerEntity entity = loadByEmail(email);
        if (entity == null || !passwordUtil.matches(request.getPassword(), entity.getPassword())) {
            return fail(CustomerErrorCode.LOGIN_FAILED);
        }

        return doLogin(entity);
    }

    @Override
    public ApiResponse<CustomerLoginResponse> loginWithCode(CustomerCodeLoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (!emailCodeService.verifyCode(email, EmailCodeScene.LOGIN, request.getCode())) {
            return fail(CustomerErrorCode.EMAIL_CODE_INVALID);
        }

        CustomerEntity entity = customerMapper.selectOne(new LambdaQueryWrapper<CustomerEntity>()
                .eq(CustomerEntity::getEmail, email)
                .last("limit 1"));
        if (entity == null) {
            return fail(CustomerErrorCode.LOGIN_FAILED);
        }

        return doLogin(entity);
    }

    @Override
    public ApiResponse<Void> logout() {
        StpUtil.logout();
        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> updatePassword(UpdateCustomerPasswordRequest request) {
        Long customerId = StpUtil.getLoginIdAsLong();

        CustomerEntity entity = customerMapper.selectOne(new LambdaQueryWrapper<CustomerEntity>()
                .select(CustomerEntity::getId, CustomerEntity::getPassword)
                .eq(CustomerEntity::getId, customerId)
                .last("limit 1"));

        if (entity == null) {
            return fail(CustomerErrorCode.CUSTOMER_NOT_FOUND);
        }
        if (!passwordUtil.matches(request.getOldPassword(), entity.getPassword())) {
            return fail(CustomerErrorCode.OLD_PASSWORD_INCORRECT);
        }

        entity.setPassword(passwordUtil.encode(request.getNewPassword()));
        if (customerMapper.updateById(entity) != 1) {
            return fail(CustomerErrorCode.UPDATE_PASSWORD_FAILED);
        }

        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> resetPassword(ResetCustomerPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (!emailCodeService.verifyCode(email, EmailCodeScene.RESET_PASSWORD, request.getCode())) {
            return fail(CustomerErrorCode.EMAIL_CODE_INVALID);
        }

        CustomerEntity entity = customerMapper.selectOne(new LambdaQueryWrapper<CustomerEntity>()
                .select(CustomerEntity::getId, CustomerEntity::getStatus)
                .eq(CustomerEntity::getEmail, email)
                .last("limit 1"));

        if (entity == null) {
            return fail(CustomerErrorCode.CUSTOMER_NOT_FOUND);
        }
        if (entity.getStatus() != null && entity.getStatus() == CustomerStatus.DISABLED.getValue()) {
            return fail(CustomerErrorCode.ACCOUNT_DISABLED);
        }

        entity.setPassword(passwordUtil.encode(request.getNewPassword()));
        if (customerMapper.updateById(entity) != 1) {
            return fail(CustomerErrorCode.UPDATE_PASSWORD_FAILED);
        }

        return ApiResponse.success();
    }

    /**
     * 校验账号状态并完成 Sa-Token 登录，返回令牌和顾客信息。
     */
    private ApiResponse<CustomerLoginResponse> doLogin(CustomerEntity entity) {
        if (entity.getStatus() == null || entity.getStatus() != CustomerStatus.AVAILABLE.getValue()) {
            return fail(CustomerErrorCode.ACCOUNT_DISABLED);
        }
        StpUtil.login(entity.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return ApiResponse.success(customerConverter.toLoginResponse(entity, tokenInfo));
    }

    /**
     * 查询顾客账号，显式带出密码字段用于校验。
     */
    private CustomerEntity loadByEmail(String email) {
        return customerMapper.selectOne(new LambdaQueryWrapper<CustomerEntity>()
                .select(CustomerEntity::getId,
                        CustomerEntity::getNickname,
                        CustomerEntity::getEmail,
                        CustomerEntity::getPassword,
                        CustomerEntity::getStatus,
                        CustomerEntity::getCreateTime,
                        CustomerEntity::getUpdateTime)
                .eq(CustomerEntity::getEmail, email)
                .last("limit 1"));
    }

    private boolean existsByEmail(String email) {
        return customerMapper.exists(new LambdaQueryWrapper<CustomerEntity>()
                .eq(CustomerEntity::getEmail, email));
    }

    private <T> ApiResponse<T> fail(CustomerErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}

package cn.dextea.customer.converter;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dextea.customer.dto.response.CustomerDetailResponse;
import cn.dextea.customer.dto.response.CustomerLoginResponse;
import cn.dextea.customer.dto.response.RegisterCustomerResponse;
import cn.dextea.customer.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerConverter {

    public RegisterCustomerResponse toRegisterResponse(CustomerEntity entity) {
        return RegisterCustomerResponse.builder()
                .id(entity.getId())
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .build();
    }

    public CustomerLoginResponse toLoginResponse(CustomerEntity entity, SaTokenInfo tokenInfo) {
        return CustomerLoginResponse.builder()
                .tokenName(tokenInfo.getTokenName())
                .tokenValue(tokenInfo.getTokenValue())
                .tokenTimeout(tokenInfo.getTokenTimeout())
                .customerId(entity.getId())
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .build();
    }

    public CustomerDetailResponse toDetailResponse(CustomerEntity entity) {
        return CustomerDetailResponse.builder()
                .id(entity.getId())
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }
}

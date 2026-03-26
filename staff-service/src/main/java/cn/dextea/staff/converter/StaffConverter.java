package cn.dextea.staff.converter;

import cn.dextea.staff.dto.response.CreateStaffResponse;
import cn.dextea.staff.dto.response.ResetStaffPasswordResponse;
import cn.dextea.staff.dto.response.StaffDetailResponse;
import cn.dextea.staff.dto.response.StaffLoginResponse;
import cn.dextea.staff.entity.StaffEntity;
import cn.dev33.satoken.stp.SaTokenInfo;
import org.springframework.stereotype.Component;

/**
 * 员工模块对象转换器。
 */
@Component
public class StaffConverter {

    public CreateStaffResponse toCreateStaffResponse(StaffEntity staffEntity, String initialPassword) {
        return CreateStaffResponse.builder()
                .id(staffEntity.getId())
                .username(staffEntity.getUsername())
                .realName(staffEntity.getRealName())
                .userType(staffEntity.getUserType())
                .status(staffEntity.getStatus())
                .initialPassword(initialPassword)
                .createTime(staffEntity.getCreateTime())
                .build();
    }

    /**
     * 将员工实体转换为通用详情响应。
     */
    public StaffDetailResponse toStaffDetailResponse(StaffEntity staffEntity) {
        return StaffDetailResponse.builder()
                .id(staffEntity.getId())
                .username(staffEntity.getUsername())
                .realName(staffEntity.getRealName())
                .userType(staffEntity.getUserType())
                .status(staffEntity.getStatus())
                .lastLoginTime(staffEntity.getLastLoginTime())
                .lastLoginIp(staffEntity.getLastLoginIp())
                .createTime(staffEntity.getCreateTime())
                .updateTime(staffEntity.getUpdateTime())
                .build();
    }

    /**
     * 组装后台重置密码响应。
     */
    public ResetStaffPasswordResponse toResetPasswordResponse(StaffEntity staffEntity, String resetPassword) {
        return ResetStaffPasswordResponse.builder()
                .id(staffEntity.getId())
                .username(staffEntity.getUsername())
                .resetPassword(resetPassword)
                .build();
    }

    /**
     * 组装员工登录成功响应。
     */
    public StaffLoginResponse toStaffLoginResponse(StaffEntity staffEntity, SaTokenInfo tokenInfo) {
        return StaffLoginResponse.builder()
                .tokenName(tokenInfo.getTokenName())
                .tokenValue(tokenInfo.getTokenValue())
                .staff(toStaffDetailResponse(staffEntity))
                .build();
    }
}

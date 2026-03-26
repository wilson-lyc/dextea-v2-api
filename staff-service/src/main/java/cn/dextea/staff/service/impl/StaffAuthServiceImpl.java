package cn.dextea.staff.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.converter.StaffConverter;
import cn.dextea.staff.dto.request.StaffLoginRequest;
import cn.dextea.staff.dto.response.StaffLoginResponse;
import cn.dextea.staff.entity.StaffEntity;
import cn.dextea.staff.enums.StaffErrorCode;
import cn.dextea.staff.enums.StaffStatus;
import cn.dextea.staff.mapper.StaffMapper;
import cn.dextea.staff.service.StaffAuthService;
import cn.dextea.staff.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 员工认证服务实现。
 */
@Service
@RequiredArgsConstructor
public class StaffAuthServiceImpl implements StaffAuthService {
    private final StaffMapper staffMapper;
    private final PasswordUtil passwordUtil;
    private final StaffConverter staffConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<StaffLoginResponse> login(StaffLoginRequest request, HttpServletRequest httpServletRequest) {
        String username = request.getUsername().trim();

        // 查询登录账号，并显式带出密码字段用于密码校验。
        StaffEntity staffEntity = staffMapper.selectOne(new LambdaQueryWrapper<StaffEntity>()
                .select(StaffEntity::getId,
                        StaffEntity::getUsername,
                        StaffEntity::getPassword,
                        StaffEntity::getRealName,
                        StaffEntity::getUserType,
                        StaffEntity::getStatus,
                        StaffEntity::getLastLoginTime,
                        StaffEntity::getLastLoginIp,
                        StaffEntity::getCreateTime,
                        StaffEntity::getUpdateTime)
                .eq(StaffEntity::getUsername, username)
                .last("limit 1"));
        if (staffEntity == null || !passwordUtil.matches(request.getPassword(), staffEntity.getPassword())) {
            return fail(StaffErrorCode.LOGIN_FAILED);
        }
        if (staffEntity.getStatus() == null || staffEntity.getStatus() != StaffStatus.AVAILABLE.getValue()) {
            return fail(StaffErrorCode.ACCOUNT_DISABLED);
        }

        // 登录成功后回写最近登录时间和来源 IP。
        staffEntity.setLastLoginTime(LocalDateTime.now());
        staffEntity.setLastLoginIp(resolveClientIp(httpServletRequest));
        staffMapper.updateById(staffEntity);

        StpUtil.login(staffEntity.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return ApiResponse.success(staffConverter.toStaffLoginResponse(staffEntity, tokenInfo));
    }

    /**
     * 提取客户端 IP，优先读取常见反向代理头。
     */
    private String resolveClientIp(HttpServletRequest request) {
        String[] headerNames = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        for (String headerName : headerNames) {
            String headerValue = request.getHeader(headerName);
            if (headerValue != null && !headerValue.isBlank() && !"unknown".equalsIgnoreCase(headerValue)) {
                return headerValue.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    private <T> ApiResponse<T> fail(StaffErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}

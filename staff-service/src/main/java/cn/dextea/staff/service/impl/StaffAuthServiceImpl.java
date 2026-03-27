package cn.dextea.staff.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.converter.StaffConverter;
import cn.dextea.staff.dto.request.StaffLoginRequest;
import cn.dextea.staff.dto.request.StaffUpdatePasswordRequest;
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
        // 先规整登录账号，避免账号首尾空格影响查询结果。
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
        // 只有可用状态的员工才允许登录后台系统。
        if (staffEntity.getStatus() == null || staffEntity.getStatus() != StaffStatus.AVAILABLE.getValue()) {
            return fail(StaffErrorCode.ACCOUNT_DISABLED);
        }

        // 登录成功后回写最近登录时间和来源 IP。
        staffEntity.setLastLoginTime(LocalDateTime.now());
        staffEntity.setLastLoginIp(resolveClientIp(httpServletRequest));
        staffMapper.updateById(staffEntity);

        // 写入 Sa-Token 登录态，并返回登录令牌及员工资料。
        StpUtil.login(staffEntity.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return ApiResponse.success(staffConverter.toStaffLoginResponse(staffEntity, tokenInfo));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> updatePassword(StaffUpdatePasswordRequest request) {
        // 先从登录态里拿到当前员工 ID，只允许本人修改自己的密码。
        Long staffId = StpUtil.getLoginIdAsLong();
        StaffEntity staffEntity = staffMapper.selectOne(new LambdaQueryWrapper<StaffEntity>()
                .select(StaffEntity::getId, StaffEntity::getPassword)
                .eq(StaffEntity::getId, staffId)
                .last("limit 1"));
        if (staffEntity == null) {
            return fail(StaffErrorCode.STAFF_NOT_FOUND);
        }

        // 旧密码校验通过后，才允许覆盖为新密码。
        if (!passwordUtil.matches(request.getOldPassword(), staffEntity.getPassword())) {
            return fail(StaffErrorCode.OLD_PASSWORD_INCORRECT);
        }

        // 新密码落库前先做加密，数据库中不保存明文密码。
        staffEntity.setPassword(passwordUtil.encode(request.getNewPassword()));
        if (staffMapper.updateById(staffEntity) != 1) {
            return fail(StaffErrorCode.UPDATE_PASSWORD_FAILED);
        }

        return ApiResponse.success();
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

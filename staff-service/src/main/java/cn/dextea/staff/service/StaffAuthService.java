package cn.dextea.staff.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.dto.request.StaffLoginRequest;
import cn.dextea.staff.dto.response.StaffLoginResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 员工认证服务。
 */
public interface StaffAuthService {
    ApiResponse<StaffLoginResponse> login(StaffLoginRequest request, HttpServletRequest httpServletRequest);
}

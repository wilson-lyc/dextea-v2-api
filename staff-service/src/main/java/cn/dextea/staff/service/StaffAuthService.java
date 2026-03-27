package cn.dextea.staff.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.staff.dto.request.StaffLoginRequest;
import cn.dextea.staff.dto.request.StaffUpdatePasswordRequest;
import cn.dextea.staff.dto.response.StaffLoginResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 员工认证服务
 */
public interface StaffAuthService {

    /**
     * 员工账号登录
     *
     * @param request 登录请求，包含用户名和密码
     * @param httpServletRequest HTTP请求对象，用于提取客户端来源信息
     * @return 返回Sa-Token令牌信息及当前员工基础资料
     */
    ApiResponse<StaffLoginResponse> login(StaffLoginRequest request, HttpServletRequest httpServletRequest);

    /**
     * 员工修改登录密码
     *
     * @param request 修改密码请求，包含原密码和新密码
     * @return 返回修改结果
     */
    ApiResponse<Void> updatePassword(StaffUpdatePasswordRequest request);
}

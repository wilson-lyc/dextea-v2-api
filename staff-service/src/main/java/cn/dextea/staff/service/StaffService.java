package cn.dextea.staff.service;

import cn.dextea.common.dto.ApiResponse;

/**
 * @author Lai Yongchao
 */
public interface StaffService {
    ApiResponse getStaffById(int id);
    ApiResponse getStaffList(int current, int size, int id, String role, String phone, int state);
    ApiResponse resetPwd(int id);
}

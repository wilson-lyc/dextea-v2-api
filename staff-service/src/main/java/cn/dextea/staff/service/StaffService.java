package cn.dextea.staff.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.staff.dto.*;

/**
 * @author Lai Yongchao
 */
public interface StaffService {
    ApiResponse create(CreateStaffDTO data);
    ApiResponse getStaffById(Long id);
    ApiResponse getStaffList(int current, int size, SearchStaffDTO data);
    ApiResponse resetPwd(Long id);
    ApiResponse update(Long id, UpdateStaffDTO data);
    ApiResponse login(CheckPwdDTO data);
}

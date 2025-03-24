package cn.dextea.staff.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.staff.dto.*;

/**
 * @author Lai Yongchao
 */
public interface StaffService {
    ApiResponse createStaff(StaffCreateDTO data);
    ApiResponse getStaffList(int current, int size, StaffQueryDTO data);
    ApiResponse getStaffInfo(Long id);
    ApiResponse updateStaffInfo(Long id, StaffUpdateDTO data);
    ApiResponse sysResetPwd(Long id);
    ApiResponse updateStaffPwd(Long id, StaffUpdatePwdDTO data);
    ApiResponse login(StaffLoginDTO data);
}

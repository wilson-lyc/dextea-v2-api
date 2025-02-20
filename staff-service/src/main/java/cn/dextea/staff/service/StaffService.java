package cn.dextea.staff.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.staff.dto.*;

/**
 * @author Lai Yongchao
 */
public interface StaffService {
    ApiResponse create(StaffCreateDTO data);
    ApiResponse getById(Long id);
    ApiResponse getList(int current, int size, StaffQueryDTO data);
    ApiResponse resetPwd(Long id);
    ApiResponse updateBase(Long id, StaffUpdateDTO data);
    ApiResponse login(StaffLoginDTO data);
    ApiResponse active(Long id);
    ApiResponse ban(Long id);
    ApiResponse updatePwd(Long id, PwdUpdateDTO data);
}

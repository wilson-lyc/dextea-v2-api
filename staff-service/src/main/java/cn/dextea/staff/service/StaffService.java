package cn.dextea.staff.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.staff.dto.UpdateStaffDTO;
import cn.dextea.staff.dto.RegisterDTO;
import cn.dextea.staff.dto.SearchStaffDTO;

/**
 * @author Lai Yongchao
 */
public interface StaffService {
    ApiResponse create(RegisterDTO data);
    ApiResponse getStaffById(Long id);
    ApiResponse search(SearchStaffDTO data);
    ApiResponse resetPwd(Long id);
    ApiResponse update(Long id, UpdateStaffDTO data);
}

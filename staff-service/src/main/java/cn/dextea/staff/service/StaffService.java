package cn.dextea.staff.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.staff.dto.*;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface StaffService {
    ApiResponse createStaff(StaffCreateDTO data);
    ApiResponse getStaffList(int current, int size, StaffQueryDTO data);
    ApiResponse getStaffInfo(Long id) throws NotFoundException;
    ApiResponse getStaffStatus(Long id) throws NotFoundException;
    ApiResponse updateStaffInfo(Long id, StaffUpdateDTO data) throws NotFoundException;
    ApiResponse updateStaffStatus(Long id, Integer status) throws NotFoundException;
    ApiResponse sysResetPwd(Long id) throws NotFoundException;
    ApiResponse updateStaffPwd(Long id, StaffUpdatePwdDTO data) throws NotFoundException;
    ApiResponse login(StaffLoginDTO data) throws IllegalAccessException;
}

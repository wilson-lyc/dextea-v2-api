package cn.dextea.staff.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.ResponseDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * @author Lai Yongchao
 */
public interface StaffService {
    ApiResponse getStaffById(int id);
    ApiResponse getStaffList(int currentPage,int pageSize);
}

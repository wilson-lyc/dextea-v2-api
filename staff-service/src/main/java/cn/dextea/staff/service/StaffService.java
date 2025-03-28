package cn.dextea.staff.service;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.staff.dto.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface StaffService {
    DexteaApiResponse<StaffCreateResponse> createStaff(StaffCreateRequest data);
    DexteaApiResponse<IPage<StaffListResponse>> getStaffList(int current, int size, StaffFilter data);
    DexteaApiResponse<StaffInfoResponse> getStaffInfo(Long id) throws NotFoundException;
    DexteaApiResponse<StaffStatusResponse> getStaffStatus(Long id) throws NotFoundException;
    DexteaApiResponse<StaffInfoResponse> updateStaffInfo(Long id, StaffUpdateRequest data) throws NotFoundException;
    DexteaApiResponse<StaffInfoResponse> updateStaffStatus(Long id, Integer status) throws NotFoundException;
    DexteaApiResponse<StaffResetPasswordResponse> sysResetPwd(Long id) throws NotFoundException;
    DexteaApiResponse<StaffInfoResponse> updateStaffPwd(Long id, StaffUpdatePwdRequest data) throws NotFoundException;
    DexteaApiResponse<StaffLoginResponse> staffLogin(StaffLoginRequest data) throws IllegalAccessException;
}

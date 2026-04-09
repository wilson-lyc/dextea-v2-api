package cn.dextea.staff.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.staff.StaffModel;
import cn.dextea.staff.model.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * @author Lai Yongchao
 */
public interface StaffService {
    DexteaApiResponse<StaffCreateResponse> createStaff(StaffCreateRequest data);
    DexteaApiResponse<IPage<StaffModel>> getStaffList(int current, int size, StaffFilter data);
    DexteaApiResponse<StaffModel> getStaffDetail(Long id);
    DexteaApiResponse<StaffModel> getStaffStatus(Long id);
    DexteaApiResponse<Void> updateStaffDetail(Long id, StaffUpdateDetailRequest data);
    DexteaApiResponse<Void> updateStaffStatus(Long id, Integer status);
    DexteaApiResponse<StaffResetPasswordResponse> sysResetPwd(Long id);
    DexteaApiResponse<Void> updateStaffPwd(Long id, StaffUpdatePwdRequest data);
}

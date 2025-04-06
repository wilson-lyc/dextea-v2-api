package cn.dextea.staff.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.staff.StaffModel;
import cn.dextea.staff.model.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface StaffService {
    DexteaApiResponse<StaffCreateResponse> createStaff(StaffCreateRequest data);
    DexteaApiResponse<IPage<StaffModel>> getStaffList(int current, int size, StaffFilter data);
    DexteaApiResponse<StaffModel> getStaffDetail(Long id) throws NotFoundException;
    DexteaApiResponse<StaffModel> getStaffStatus(Long id) throws NotFoundException;
    DexteaApiResponse<Void> updateStaffDetail(Long id, StaffUpdateDetailRequest data) throws NotFoundException;
    DexteaApiResponse<Void> updateStaffStatus(Long id, Integer status) throws NotFoundException;
    DexteaApiResponse<StaffResetPasswordResponse> sysResetPwd(Long id) throws NotFoundException;
    DexteaApiResponse<Void> updateStaffPwd(Long id, StaffUpdatePwdRequest data) throws NotFoundException;
}

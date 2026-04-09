package cn.dextea.staff.service;

import cn.dextea.common.model.staff.StaffModel;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface InternalService {
    boolean isStaffIdValid(Long id);
    List<StaffModel> getStaffInIds(List<Long> ids);
    boolean isPasswordValid(Long id, String password);
}

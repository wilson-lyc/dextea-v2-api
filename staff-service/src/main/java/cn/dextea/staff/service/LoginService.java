package cn.dextea.staff.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.staff.StaffModel;
import cn.dextea.staff.model.StaffLoginRequest;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface LoginService {
    DexteaApiResponse<StaffModel> staffLogin(@Valid StaffLoginRequest data);
}

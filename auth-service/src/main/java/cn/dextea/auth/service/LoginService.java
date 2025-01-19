package cn.dextea.auth.service;

import cn.dextea.auth.dto.StaffLoginDTO;
import cn.dextea.common.dto.ApiResponse;

/**
 * @author Lai Yongchao
 */
public interface LoginService {
    ApiResponse staffLogin(StaffLoginDTO data);
}

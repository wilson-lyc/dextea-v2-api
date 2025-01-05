package cn.dextea.staff.service;

import cn.dextea.common.dto.ApiResponse;

public interface RegisterService {
    ApiResponse register(String name, String role, String phone);
}

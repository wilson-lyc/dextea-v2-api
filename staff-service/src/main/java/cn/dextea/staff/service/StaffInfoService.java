package cn.dextea.staff.service;

import cn.dextea.common.dto.ResponseDTO;
import jakarta.validation.constraints.NotBlank;

public interface StaffInfoService {
    ResponseDTO register(String name, String role, String phone);
}

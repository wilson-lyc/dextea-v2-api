package cn.dextea.staff.feign;

import cn.dextea.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("auth-service")
public interface RoleFeign {
    @GetMapping("/role/getStaffRoleKey")
    ApiResponse getStaffRoleKeyByUid(@RequestParam("uid") Long uid);
}

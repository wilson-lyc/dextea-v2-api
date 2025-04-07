package cn.dextea.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@FeignClient("auth-service")
public interface AuthFeign {
    @GetMapping("/auth/internal/getStaffRoleKeys")
    List<String> getStaffRoleKeys(@RequestParam("id") Long id);
    @GetMapping("/auth/internal/getStaffPermissionKeys")
    List<String> getStaffPermissionKeys(@RequestParam("id") Long id);
}

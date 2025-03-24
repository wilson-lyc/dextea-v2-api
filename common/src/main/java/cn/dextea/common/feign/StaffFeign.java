package cn.dextea.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lai Yongchao
 */
@FeignClient("staff-service")
public interface StaffFeign {
    @GetMapping("/staff/internal/isStaffIdValid")
    boolean isStaffIdValid(@RequestParam("id") Long id);
}

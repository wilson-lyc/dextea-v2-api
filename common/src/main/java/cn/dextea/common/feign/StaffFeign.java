package cn.dextea.common.feign;

import cn.dextea.common.model.staff.StaffModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@FeignClient("staff-service")
public interface StaffFeign {
    @GetMapping("/staff/internal/isStaffIdValid")
    boolean isStaffIdValid(@RequestParam("id") Long id);
    @GetMapping("/staff/internal/getStaffInIds")
    List<StaffModel> getStaffInIds(@RequestParam("ids") List<Long> ids);
}

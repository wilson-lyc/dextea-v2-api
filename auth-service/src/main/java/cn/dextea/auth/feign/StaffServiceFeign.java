package cn.dextea.auth.feign;

import cn.dextea.auth.dto.StaffLoginDTO;
import cn.dextea.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Lai Yongchao
 */
@FeignClient(name = "staff-service")
public interface StaffServiceFeign {
    /**
     * 检查密码
     * @param data {id, password}
     */
    @PostMapping("/staff/login")
    ApiResponse login(@RequestBody StaffLoginDTO data);
}

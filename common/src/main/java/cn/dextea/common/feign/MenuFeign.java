package cn.dextea.common.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lai Yongchao
 */
@FeignClient("menu-service")
public interface MenuFeign {
    @GetMapping("/menu/internal/isMenuIdValid")
    boolean isMenuIdValid(@RequestParam("id") Long id);
}

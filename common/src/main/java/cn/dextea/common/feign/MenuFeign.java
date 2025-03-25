package cn.dextea.common.feign;

import cn.dextea.common.pojo.Menu;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Lai Yongchao
 */
@FeignClient("menu-service")
public interface MenuFeign {
    @GetMapping("/menu/internal/getMenuById")
    Menu getMenuById(@RequestParam("id") Long id);
}

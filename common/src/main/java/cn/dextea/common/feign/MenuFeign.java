package cn.dextea.common.feign;

import cn.dextea.common.model.menu.MenuModel;
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

    @GetMapping("/menu/internal/getMenuDetail")
    MenuModel getMenuDetail(@RequestParam("id")  Long id,
                            @RequestParam("mode") String mode);

    @GetMapping("/menu/internal/getMenuDetail")
    MenuModel getMenuDetail(@RequestParam("id")  Long id,
                            @RequestParam("mode") String mode,
                            @RequestParam("storeId") Long storeId);
}

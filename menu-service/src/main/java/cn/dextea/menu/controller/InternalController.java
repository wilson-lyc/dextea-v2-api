package cn.dextea.menu.controller;

import cn.dextea.common.pojo.Menu;
import cn.dextea.menu.service.InternalService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lai Yongchao
 */
@RestController
public class InternalController {
    @Resource
    private InternalService internalService;
    @GetMapping("/menu/internal/getMenuById")
    public Menu getMenuById(@RequestParam  Long id) {
        return internalService.getMenuById(id);
    }
}

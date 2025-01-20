package cn.dextea.auth.controller;

import cn.dextea.auth.dto.RoleDTO;
import cn.dextea.auth.service.RoleService;
import cn.dextea.auth.service.RouterService;
import cn.dextea.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class RouterController {
    @Autowired
    RouterService routerService;

    /**
     * 获取路由列表`
     */
    @GetMapping("/router")
    public ApiResponse getRouterList() {
        return routerService.getRouterList();
    }

    @GetMapping("/router/getStaffRouter")
    public ApiResponse getStaffRouter(@RequestParam Long uid) {
        return routerService.getStaffRouter(uid);
    }
}

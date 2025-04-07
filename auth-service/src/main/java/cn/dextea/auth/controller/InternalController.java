package cn.dextea.auth.controller;

import cn.dextea.auth.service.InternalService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@RestController
public class InternalController {
    @Resource
    private InternalService internalService;
    @GetMapping("/auth/internal/getStaffRoleKeys")
    public List<String> getStaffRoleKeys(@RequestParam Long id) {
        return internalService.getStaffRoleKeys(id);
    }

    @GetMapping("/auth/internal/getStaffPermissionKeys")
    public List<String> getStaffPermissionKeys(@RequestParam Long id) {
        return internalService.getStaffPermissionKeys(id);
    }
}

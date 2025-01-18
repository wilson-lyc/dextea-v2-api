package cn.dextea.auth.controller;

import cn.dextea.auth.pojo.Permission;
import cn.dextea.auth.service.PermissionService;
import cn.dextea.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@RestController
public class PermissionController {
    @Autowired
    PermissionService permissionService;
    @GetMapping("/auth/permission")
    public ApiResponse getPermissionList() {
        return permissionService.getPermissionList();
    }
}

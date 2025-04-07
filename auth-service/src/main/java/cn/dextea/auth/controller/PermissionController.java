package cn.dextea.auth.controller;

import cn.dextea.auth.service.PermissionService;
import cn.dextea.common.model.auth.PermissionModel;
import cn.dextea.common.model.common.DexteaApiResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@RestController
public class PermissionController {
    @Resource
    private PermissionService permissionService;
    @GetMapping("/permission")
    public DexteaApiResponse<List<PermissionModel>> getPermissionList() {
        return permissionService.getPermissionList();
    }
}

package cn.dextea.auth.controller;

import cn.dextea.auth.dto.RoleDTO;
import cn.dextea.auth.pojo.Role;
import cn.dextea.auth.service.RoleService;
import cn.dextea.common.dto.ApiResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    RoleService roleService;

    /**
     * 创建角色
     * @param data {key,description}
     */
    @PostMapping("")
    public ApiResponse create(@Valid @RequestBody RoleDTO data) {
        return roleService.create(data);
    }

    /**
     * 获取角色列表
     * @param current 当前页
     * @param size 每页大小
     */
    @GetMapping("")
    public ApiResponse getRoleList(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "10") int size) {
        return roleService.getRoleList(current,size);
    }

    /**
     * 根据id获取角色详情
     * @param id 角色ID
     */
    @GetMapping("/{id:\\d+}")
    public ApiResponse getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    /**
     * 更新角色
     * @param id 角色ID
     * @param data {key,label,description}
     */
    @PutMapping("/{id:\\d+}")
    public ApiResponse update(@PathVariable Long id, @Valid @RequestBody RoleDTO data) {
        return roleService.update(id, data);
    }

    /**
     * 获取员工所有角色
     * @param uid 员工ID
     */
    @GetMapping("/getStaffRole")
    public ApiResponse getStaffRoleByUid(@RequestParam Long uid) {
        return roleService.getStaffRoleByUid(uid);
    }

    /**
     * 获取员工所有角色Key
     * @param uid 员工ID
     */
    @GetMapping("/getStaffRoleKey")
    public ApiResponse getStaffRoleKeyByUid(@RequestParam Long uid) {
        return roleService.getStaffRoleKeyByUid(uid);
    }

    @GetMapping("/getStaffRouter")
    public ApiResponse getStaffRouterByUid(@RequestParam Long uid) {
        return roleService.getStaffRouterByUid(uid);
    }
}

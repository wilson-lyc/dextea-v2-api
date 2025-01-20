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
public class RoleController {
    @Autowired
    RoleService roleService;

    /**
     * 创建角色
     * @param data {key,description}
     */
    @PostMapping("/role")
    public ApiResponse create(@Valid @RequestBody RoleDTO data) {
        return roleService.create(data);
    }

    /**
     * 获取角色列表
     * @param current 当前页
     * @param size 每页大小
     */
    @GetMapping("/role")
    public ApiResponse getRoleList(@RequestParam(defaultValue = "1") int current, @RequestParam(defaultValue = "10") int size) {
        return roleService.getRoleList(current,size);
    }

    /**
     * 获取角色详情
     * @param id 角色ID
     */
    @GetMapping("/role/{id:\\d+}")
    public ApiResponse getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    /**
     * 更新角色
     * @param id 角色ID
     * @param data {key,label,description}
     */
    @PutMapping("/role/{id:\\d+}")
    public ApiResponse update(@PathVariable Long id, @Valid @RequestBody RoleDTO data) {
        return roleService.update(id, data);
    }

    /**
     * 获取员工角色
     * @param uid 员工ID
     */
    @GetMapping("/role/getStaffRole")
    public ApiResponse getRoleByStaffId(@RequestParam Long uid) {
        return roleService.getRoleByStaffId(uid);
    }
}

package cn.dextea.auth.controller;

import cn.dextea.auth.dto.role.RoleCreateDTO;
import cn.dextea.auth.dto.role.RoleUpdateDTO;
import cn.dextea.auth.service.RoleService;
import cn.dextea.common.model.common.ApiResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class RoleController {
    @Resource
    private RoleService roleService;
    @PostMapping("/role")
    public ApiResponse createRole(@Valid @RequestBody RoleCreateDTO data){
        return roleService.createRole(data);
    }

    @GetMapping("/role")
    public ApiResponse getRoleList(){
        return roleService.getRoleList();
    }

    @GetMapping("/role/{id:\\d+}")
    public ApiResponse getRoleById(@PathVariable Long id) throws NotFoundException {
        return roleService.getRoleById(id);
    }

    @GetMapping("/role/{id:\\d+}/base")
    public ApiResponse getRoleBase(@PathVariable Long id) throws NotFoundException {
        return roleService.getRoleBase(id);
    }

    @PutMapping("/role/{id:\\d+}/base")
    public ApiResponse updateRoleBase(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateDTO data) throws NotFoundException {
        return roleService.updateRoleBase(id, data);
    }
}

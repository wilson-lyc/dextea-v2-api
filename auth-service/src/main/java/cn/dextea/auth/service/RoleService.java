package cn.dextea.auth.service;

import cn.dextea.auth.dto.role.RoleCreateDTO;
import cn.dextea.auth.dto.role.RoleUpdateDTO;
import cn.dextea.common.model.common.ApiResponse;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface RoleService {
    ApiResponse createRole(RoleCreateDTO data);
    ApiResponse getRoleList();
    ApiResponse getRoleById(Long id) throws NotFoundException;
    ApiResponse getRoleBase(Long id) throws NotFoundException;
    ApiResponse updateRoleBase(Long id,RoleUpdateDTO data) throws NotFoundException;
}

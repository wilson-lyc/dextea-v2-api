package cn.dextea.auth.service.impl;

import cn.dextea.auth.dto.role.RoleBaseDTO;
import cn.dextea.auth.dto.role.RoleCreateDTO;
import cn.dextea.auth.dto.role.RoleListDTO;
import cn.dextea.auth.dto.role.RoleUpdateDTO;
import cn.dextea.auth.mapper.RoleMapper;
import cn.dextea.auth.service.RoleService;
import cn.dextea.common.model.common.ApiResponse;
import cn.dextea.auth.pojo.Role;
import com.alibaba.fastjson2.JSONObject;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleMapper roleMapper;

    @Override
    public ApiResponse createRole(RoleCreateDTO data) {
        Role role=data.toRole();
        roleMapper.insert(role);
        return ApiResponse.success("角色创建成功", JSONObject.of("role",role));
    }

    @Override
    public ApiResponse getRoleList() {
        MPJLambdaWrapper<Role> wrapper=new MPJLambdaWrapper<Role>()
                .selectAsClass(Role.class, RoleListDTO.class);
        List<RoleListDTO> roles=roleMapper.selectJoinList(RoleListDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("roles",roles));
    }

    @Override
    public ApiResponse getRoleById(Long id) throws NotFoundException {
        Role role=roleMapper.selectById(id);
        if (Objects.isNull(role))
            throw new NotFoundException("角色不存在");
        return ApiResponse.success(JSONObject.of("role",role));
    }

    @Override
    public ApiResponse getRoleBase(Long id) throws NotFoundException {
        MPJLambdaWrapper<Role> wrapper=new MPJLambdaWrapper<Role>()
                .selectAsClass(Role.class, RoleBaseDTO.class)
                .eq(Role::getId,id);
        RoleBaseDTO role=roleMapper.selectJoinOne(RoleBaseDTO.class,wrapper);
        if (Objects.isNull(role))
            throw new NotFoundException("角色不存在");
        return ApiResponse.success(JSONObject.of("role",role));
    }

    @Override
    public ApiResponse updateRoleBase(Long id, RoleUpdateDTO data) throws NotFoundException {
        Role role=data.toRole(id);
        if (roleMapper.updateById(role)==0)
            throw new NotFoundException("角色不存在");
        return ApiResponse.success("更新成功");
    }
}

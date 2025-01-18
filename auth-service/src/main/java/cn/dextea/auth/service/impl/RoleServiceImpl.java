package cn.dextea.auth.service.impl;

import cn.dextea.auth.dto.RoleDTO;
import cn.dextea.auth.mapper.RoleMapper;
import cn.dextea.auth.pojo.Role;
import cn.dextea.auth.service.RoleService;
import cn.dextea.common.dto.ApiResponse;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Lai Yongchao
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleMapper roleMapper;

    @Override
    public ApiResponse create(RoleDTO data) {
        Role role = data.toRole();
        roleMapper.insert(role);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse getRoleList(int current, int size) {
        Page<Role> page = new Page<>(current, size);
        page=roleMapper.selectPage(page,null);
        if (page.getCurrent()>page.getPages()){
            page.setCurrent(page.getPages());
            page=roleMapper.selectPage(page,null);
        }
        return ApiResponse.success(JSONObject.from(page));
    }

    @Override
    public ApiResponse getRoleById(Long id) {
        Role role=roleMapper.selectById(id);
        if (role==null){
            String msg=String.format("角色不存在，ID=%d",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success(JSONObject.of("role",role));
    }

    @Override
    public ApiResponse update(Long id, RoleDTO data) {
        Role role=Role.builder()
                .id(id)
                .label(data.getLabel())
                .description(data.getDescription())
                .build();
        int num=roleMapper.updateById(role);
        if (num==0){
            String msg=String.format("角色不存在，ID=%d",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success();
    }
}

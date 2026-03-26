package cn.dextea.staff.converter;

import cn.dextea.staff.dto.response.CreateRoleResponse;
import cn.dextea.staff.dto.response.RoleDetailResponse;
import cn.dextea.staff.entity.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleConverter {

    public CreateRoleResponse toCreateRoleResponse(RoleEntity roleEntity) {
        return CreateRoleResponse.builder()
                .id(roleEntity.getId())
                .name(roleEntity.getName())
                .remark(roleEntity.getRemark())
                .dataScope(roleEntity.getDataScope())
                .status(roleEntity.getStatus())
                .createTime(roleEntity.getCreateTime())
                .build();
    }

    public RoleDetailResponse toRoleDetailResponse(RoleEntity roleEntity) {
        return RoleDetailResponse.builder()
                .id(roleEntity.getId())
                .name(roleEntity.getName())
                .remark(roleEntity.getRemark())
                .dataScope(roleEntity.getDataScope())
                .status(roleEntity.getStatus())
                .createTime(roleEntity.getCreateTime())
                .updateTime(roleEntity.getUpdateTime())
                .build();
    }
}

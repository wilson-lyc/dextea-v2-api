package cn.dextea.staff.converter;

import cn.dextea.staff.dto.response.PermissionDetailResponse;
import cn.dextea.staff.entity.PermissionEntity;
import org.springframework.stereotype.Component;

@Component
public class PermissionConverter {

    public PermissionDetailResponse toPermissionDetailResponse(PermissionEntity permissionEntity) {
        return PermissionDetailResponse.builder()
                .id(permissionEntity.getId())
                .name(permissionEntity.getName())
                .remark(permissionEntity.getRemark())
                .createTime(permissionEntity.getCreateTime())
                .build();
    }
}

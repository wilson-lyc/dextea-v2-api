package cn.dextea.staff.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("role_permission_rel")
public class RolePermissionRelEntity {
    @TableField("role_id")
    private Long roleId;

    @TableField("permission_name")
    private String permissionName;

    @TableField("create_time")
    private LocalDateTime createTime;
}

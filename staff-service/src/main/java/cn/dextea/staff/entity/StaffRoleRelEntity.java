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
@TableName("staff_role_rel")
public class StaffRoleRelEntity {
    @TableField("staff_id")
    private Long staffId;

    @TableField("role_id")
    private Long roleId;

    @TableField("create_time")
    private LocalDateTime createTime;
}

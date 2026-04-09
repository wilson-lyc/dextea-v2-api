package cn.dextea.staff.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("staff")
public class StaffEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    @TableField(select = false)
    private String password;

    @TableField("real_name")
    private String realName;

    @TableField("user_type")
    private Integer userType;

    private Integer status;

    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @TableField("last_login_ip")
    private String lastLoginIp;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}

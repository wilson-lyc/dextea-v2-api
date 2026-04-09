package cn.dextea.staff.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("permission")
public class PermissionEntity {
    @TableId("id")
    private Long id;

    private String name;

    private String remark;

    @TableField("create_time")
    private LocalDateTime createTime;
}

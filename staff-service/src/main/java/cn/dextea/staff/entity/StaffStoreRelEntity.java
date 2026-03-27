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
@TableName("staff_store_rel")
public class StaffStoreRelEntity {
    @TableField("staff_id")
    private Long staffId;

    @TableField("store_id")
    private Long storeId;

    @TableField("create_time")
    private LocalDateTime createTime;
}

package cn.dextea.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("store_menu_rel")
public class StoreMenuRelEntity {

    @TableField("store_id")
    private Long storeId;

    @TableField("menu_id")
    private Long menuId;

    @TableField("create_time")
    private LocalDateTime createTime;
}

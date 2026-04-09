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
@TableName("store_customization_item_rel")
public class StoreCustomizationItemRelEntity {

    @TableField("store_id")
    private Long storeId;

    @TableField("item_id")
    private Long itemId;

    @TableField("create_time")
    private LocalDateTime createTime;
}

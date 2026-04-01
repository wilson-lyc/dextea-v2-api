package cn.dextea.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("store_ingredient_inventory")
public class StoreIngredientInventoryEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("store_id")
    private Long storeId;

    @TableField("ingredient_id")
    private Long ingredientId;

    /** Current stock quantity. */
    private BigDecimal quantity;

    /** Unit of measure (e.g. g, kg, ml). */
    private String unit;

    /** Low-stock alert threshold — alert when quantity <= warnThreshold. */
    @TableField("warn_threshold")
    private BigDecimal warnThreshold;

    /** Timestamp of the most recent stock increase (restock). */
    @TableField("last_restock_time")
    private LocalDateTime lastRestockTime;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}

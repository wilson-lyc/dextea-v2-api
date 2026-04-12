package cn.dextea.product.entity;

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

    private Long storeId;

    private Long ingredientId;

    private BigDecimal quantity;

    private String unit;

    private BigDecimal warnThreshold;

    private LocalDateTime lastRestockTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

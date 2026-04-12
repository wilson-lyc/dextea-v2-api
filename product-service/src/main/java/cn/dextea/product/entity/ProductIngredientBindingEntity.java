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
@TableName("product_ingredient_binding")
public class ProductIngredientBindingEntity {

    private Long productId;

    private Long ingredientId;

    private BigDecimal quantity;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

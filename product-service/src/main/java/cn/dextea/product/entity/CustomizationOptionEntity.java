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
@TableName("customization_option")
public class CustomizationOptionEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long itemId;

    private String name;

    private BigDecimal price;

    private Long ingredientId;

    private BigDecimal ingredientQuantity;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

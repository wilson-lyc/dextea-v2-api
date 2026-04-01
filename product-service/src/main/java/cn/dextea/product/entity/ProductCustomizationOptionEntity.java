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
@TableName("product_customization_option")
public class ProductCustomizationOptionEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("item_id")
    private Long itemId;

    private String name;

    @TableField("price_adjustment")
    private BigDecimal priceAdjustment;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("is_default")
    private Boolean isDefault;

    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}

package cn.dextea.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@AllArgsConstructor
@NoArgsConstructor
@TableName("product_customization_item_binding")
public class ProductCustomizationItemBindingEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("product_id")
    private Long productId;

    @TableField("item_id")
    private Long itemId;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("create_time")
    private LocalDateTime createTime;
}

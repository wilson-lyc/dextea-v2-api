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
@TableName("ingredient")
public class IngredientEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String unit;

    @TableField("shelf_life")
    private Integer shelfLife;

    @TableField("shelf_life_unit")
    private Integer shelfLifeUnit;

    @TableField("storage_method")
    private Integer storageMethod;

    @TableField("prepared_expiry")
    private Integer preparedExpiry;

    @TableField("prepared_expiry_unit")
    private Integer preparedExpiryUnit;

    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}

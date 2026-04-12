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

    private Integer storageDuration;

    private Integer storageDurationUnit;

    private Integer storageMethod;

    private Integer preparedExpiry;

    private Integer preparedExpiryUnit;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

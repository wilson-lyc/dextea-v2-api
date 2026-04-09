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
@TableName("product")
public class ProductEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

package cn.dextea.product.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("s_product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;//商品ID
    private String name;//名称
    private String description;//描述
    private BigDecimal price;//价格
    private Long typeId;//品类ID
    @TableField(exist = false)
    private String typeName;//品类名称
    private String cover;//封面
}

package cn.dextea.product.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("s_customize_option")
public class CustomizeOption {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer sort;
    private Integer status;
    private Long itemId;
}

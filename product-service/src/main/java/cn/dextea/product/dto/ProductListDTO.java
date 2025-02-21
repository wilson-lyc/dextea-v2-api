package cn.dextea.product.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListDTO {
    private Long id;//商品ID
    private String name;//名称
    private String description;//描述
    private BigDecimal price;//价格
    private String categoryName;//品类名称
    private Integer status;//状态
}

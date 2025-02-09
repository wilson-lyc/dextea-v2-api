package cn.dextea.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;//商品ID
    private String name;//名称
    private String description;//描述
    private BigDecimal price;//价格
    private Long typeId;//品类ID
    private String typeName;//品类名称
}

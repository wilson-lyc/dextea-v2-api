package cn.dextea.product.dto;

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
public class ProductBaseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer status;
    private Long categoryId;
    private String categoryName;
}

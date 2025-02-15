package cn.dextea.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuProductDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer sort;
}

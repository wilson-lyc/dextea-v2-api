package cn.dextea.product.dto.option;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionListDTO {
    private Long id;
    private String name;
    private Double price;
    private Integer sort;
    private Integer status;
}

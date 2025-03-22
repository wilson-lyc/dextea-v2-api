package cn.dextea.product.dto.option;

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
public class OptionBaseDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer sort;
    private Integer globalStatus;
    private Long itemId;
    private String itemName;
    private String createTime;
    private String updateTime;
}

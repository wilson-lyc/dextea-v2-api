package cn.dextea.order.pojo;

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
public class CustomizeSelected {
    private Long itemId;
    private String itemName;
    private Long optionId;
    private String optionName;
    private BigDecimal price;
}

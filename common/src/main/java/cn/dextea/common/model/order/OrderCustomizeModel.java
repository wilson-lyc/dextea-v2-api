package cn.dextea.common.model.order;

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
@NoArgsConstructor
@AllArgsConstructor
public class OrderCustomizeModel {
    private Long itemId;
    private String itemName;
    private Long optionId;
    private String optionName;
    private BigDecimal price;
}

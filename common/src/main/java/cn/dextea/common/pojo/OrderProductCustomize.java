package cn.dextea.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Lai Yongchao
 * 订单中商品的客制化信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductCustomize {
    private Long itemId;
    private String itemName;
    private Long optionId;
    private String optionName;
    private BigDecimal price;
}

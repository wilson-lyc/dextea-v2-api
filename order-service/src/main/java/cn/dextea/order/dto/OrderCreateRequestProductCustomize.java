package cn.dextea.order.dto;

import jakarta.validation.constraints.NotNull;
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
public class OrderCreateRequestProductCustomize {
    @NotNull(message = "itemId不能为空")
    private Long itemId;
    private String itemName;
    @NotNull(message = "optionId不能为空")
    private Long optionId;
    private String optionName;
}

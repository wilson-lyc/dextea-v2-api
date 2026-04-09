package cn.dextea.order.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequestCustomize {
    @NotNull(message = "itemId不能为空")
    private Long itemId;
    @NotBlank(message = "itemName不能为空")
    private String itemName;
    @NotNull(message = "optionId不能为空")
    private Long optionId;
    @NotBlank(message = "optionName不能为空")
    private String optionName;
}

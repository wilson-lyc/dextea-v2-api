package cn.dextea.order.model;

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
    @NotNull(message = "缺少项目ID")
    private Long itemId;
    private String itemName;
    @NotNull(message = "缺少选项ID")
    private Long optionId;
    private String optionName;
}

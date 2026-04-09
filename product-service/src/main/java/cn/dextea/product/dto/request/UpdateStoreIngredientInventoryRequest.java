package cn.dextea.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoreIngredientInventoryRequest {

    @NotNull(message = "门店ID不能为空")
    @Min(value = 1, message = "门店ID无效")
    private Long storeId;

    @NotNull(message = "库存数量不能为空")
    @DecimalMin(value = "0", message = "库存数量不能小于0")
    private BigDecimal quantity;

    @DecimalMin(value = "0", message = "预警阈值不能小于0")
    private BigDecimal warnThreshold;
}

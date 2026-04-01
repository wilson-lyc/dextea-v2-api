package cn.dextea.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetInventoryRequest {

    @NotNull(message = "库存数量不能为空")
    @DecimalMin(value = "0.00", message = "库存数量不能为负")
    private BigDecimal quantity;

    @NotBlank(message = "单位不能为空")
    @Size(max = 16, message = "单位长度不能超过16位")
    private String unit;

    @NotNull(message = "预警阈值不能为空")
    @DecimalMin(value = "0.00", message = "预警阈值不能为负")
    private BigDecimal warnThreshold;
}

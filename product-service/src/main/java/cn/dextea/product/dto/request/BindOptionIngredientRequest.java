package cn.dextea.product.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class BindOptionIngredientRequest {

    @NotNull(message = "原料ID不能为空")
    @Min(value = 1, message = "原料ID不合法")
    private Long ingredientId;

    @NotNull(message = "消耗数量不能为空")
    @DecimalMin(value = "0.01", message = "消耗数量必须大于0")
    private BigDecimal quantity;

    @NotBlank(message = "计量单位不能为空")
    @Size(max = 16, message = "计量单位长度不能超过16位")
    private String unit;
}

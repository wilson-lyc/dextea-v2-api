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
public class BindProductIngredientRequest {

    @NotNull(message = "原料ID不能为空")
    private Long ingredientId;

    @NotNull(message = "用量不能为空")
    @DecimalMin(value = "0.01", message = "用量不能小于0.01")
    private BigDecimal quantity;
}

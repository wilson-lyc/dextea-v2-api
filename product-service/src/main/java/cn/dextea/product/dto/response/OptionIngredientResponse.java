package cn.dextea.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionIngredientResponse {

    private Long ingredientId;
    private String ingredientName;
    private BigDecimal quantity;
    private String unit;
}

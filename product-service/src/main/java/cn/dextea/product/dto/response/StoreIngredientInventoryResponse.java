package cn.dextea.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreIngredientInventoryResponse {

    private Long ingredientId;
    private String ingredientName;
    private String unit;
    private Integer shelfLife;
    private Integer shelfLifeUnit;
    private Integer storageMethod;
    private Integer preparedExpiry;
    private Integer preparedExpiryUnit;
    private BigDecimal quantity;
    private BigDecimal warnThreshold;
    private LocalDateTime lastRestockTime;
}

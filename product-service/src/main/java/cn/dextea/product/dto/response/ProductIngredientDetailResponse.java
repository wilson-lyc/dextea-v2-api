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
public class ProductIngredientDetailResponse {

    private Long ingredientId;
    private String ingredientName;
    private String unit;
    private Integer storageDuration;
    private Integer storageDurationUnit;
    private Integer storageMethod;
    private BigDecimal quantity;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

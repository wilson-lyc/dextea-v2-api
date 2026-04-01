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
public class CustomizationOptionDetailResponse {

    private Long id;
    private Long itemId;
    private String name;
    private BigDecimal priceAdjustment;
    private Integer sortOrder;
    private Boolean isDefault;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private OptionIngredientResponse ingredient;
}

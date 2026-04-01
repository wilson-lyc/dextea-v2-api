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
public class InventoryDetailResponse {

    private Long id;
    private Long storeId;
    private Long ingredientId;

    private String ingredientName;
    private Integer shelfLife;
    private Integer shelfLifeUnit;
    private Integer storageMethod;

    private BigDecimal quantity;
    private String unit;
    private BigDecimal warnThreshold;

    /** True when quantity <= warnThreshold. */
    private Boolean lowStock;

    private LocalDateTime lastRestockTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

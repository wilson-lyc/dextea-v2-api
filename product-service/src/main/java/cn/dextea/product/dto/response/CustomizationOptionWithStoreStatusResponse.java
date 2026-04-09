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
public class CustomizationOptionWithStoreStatusResponse {

    private Long id;
    private Long itemId;
    private String name;
    private BigDecimal price;
    private Long ingredientId;
    private BigDecimal ingredientQuantity;
    private Integer storeStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

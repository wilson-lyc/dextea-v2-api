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
public class CustomizationOptionBizDetailResponse {

    private Long id;
    private Long itemId;
    private String name;
    private BigDecimal price;
    private Integer storeStatus;
}

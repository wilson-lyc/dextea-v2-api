package cn.dextea.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBizDetailResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer storeStatus;
    private List<CustomizationItemBizDetailResponse> items;
}

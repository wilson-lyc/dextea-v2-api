package cn.dextea.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProductDetailResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer globalStatus;
    private Integer storeStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<CustomizationItemBizDetailResponse> customizationItems;
}

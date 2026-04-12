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
public class ProductDetailResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    /** 商品全局状态（ProductStatus） */
    private Integer status;
    /** 商品在当前门店的在售状态（StoreProductStatus），仅在门店上下文下返回 */
    private Integer storeStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

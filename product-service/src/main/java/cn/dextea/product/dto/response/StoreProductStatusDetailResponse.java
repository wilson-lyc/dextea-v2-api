package cn.dextea.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreProductStatusDetailResponse {

    private Long storeId;

    private Long productId;

    /**
     * 门店商品销售状态：0=售罄，1=在售
     */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

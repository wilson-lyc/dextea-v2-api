package cn.dextea.product.dto.response;

import cn.dextea.product.enums.StoreProductSaleStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StoreProductStatusDetailResponse {

    private Long id;

    private Long storeId;

    private Long productId;

    private String productName;

    private Integer status;

    private String statusLabel;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public static String getStatusLabel(Integer status) {
        if (status == null) {
            return null;
        }
        for (StoreProductSaleStatus saleStatus : StoreProductSaleStatus.values()) {
            if (saleStatus.getValue() == status) {
                return saleStatus.getLabel();
            }
        }
        return null;
    }
}
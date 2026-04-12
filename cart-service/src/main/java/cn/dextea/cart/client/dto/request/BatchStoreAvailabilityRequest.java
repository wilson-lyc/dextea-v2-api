package cn.dextea.cart.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchStoreAvailabilityRequest {

    private Long storeId;
    private List<ProductAvailabilityItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAvailabilityItem {
        private Long productId;
        private List<Long> optionIds;
    }
}

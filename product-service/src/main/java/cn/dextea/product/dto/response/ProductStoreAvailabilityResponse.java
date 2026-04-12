package cn.dextea.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStoreAvailabilityResponse {

    private Long productId;
    private boolean productAvailable;
    private List<Long> unavailableOptionIds;
}

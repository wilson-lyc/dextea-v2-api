package cn.dextea.cart.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStoreAvailabilityResponse {

    private Long productId;
    private boolean productAvailable;
    private List<Long> unavailableOptionIds;
}

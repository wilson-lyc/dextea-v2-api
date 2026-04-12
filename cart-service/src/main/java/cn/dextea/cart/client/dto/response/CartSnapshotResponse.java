package cn.dextea.cart.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartSnapshotResponse {

    private Long productId;
    private String productName;
    private BigDecimal basePrice;
    private List<CartOptionSnapshotResponse> options;
}

package cn.dextea.cart.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartOptionSnapshotResponse {

    private Long optionId;
    private Long itemId;
    private String itemName;
    private String optionName;
    private BigDecimal optionPrice;
}

package cn.dextea.cart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemSelectionDetailResponse {

    private Long itemId;
    private String itemName;
    private Long optionId;
    private String optionName;
    private BigDecimal optionPrice;
}

package cn.dextea.cart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDetailResponse {

    private List<CartItemDetailResponse> items;
    private Integer totalQuantity;
    private BigDecimal totalPrice;
}

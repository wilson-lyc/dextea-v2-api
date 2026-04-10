package cn.dextea.cart.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    private String skuId;
    private Long productId;
    private String productName;
    private BigDecimal basePrice;
    private Integer quantity;
    private List<CartItemSelection> selections;
    private BigDecimal unitPrice;
    private LocalDateTime addedAt;
}

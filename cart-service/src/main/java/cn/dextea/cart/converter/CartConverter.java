package cn.dextea.cart.converter;

import cn.dextea.cart.dto.response.CartDetailResponse;
import cn.dextea.cart.dto.response.CartItemDetailResponse;
import cn.dextea.cart.dto.response.CartItemSelectionDetailResponse;
import cn.dextea.cart.model.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartConverter {

    public CartItemDetailResponse toCartItemDetailResponse(CartItem item) {
        List<CartItemSelectionDetailResponse> selections = item.getSelections().stream()
                .map(sel -> CartItemSelectionDetailResponse.builder()
                        .itemId(sel.getItemId())
                        .itemName(sel.getItemName())
                        .optionId(sel.getOptionId())
                        .optionName(sel.getOptionName())
                        .optionPrice(sel.getOptionPrice())
                        .build())
                .toList();

        BigDecimal subtotal = item.getUnitPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemDetailResponse.builder()
                .skuId(item.getSkuId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(subtotal)
                .selections(selections)
                .build();
    }

    public CartDetailResponse toCartDetailResponse(List<CartItem> items) {
        List<CartItemDetailResponse> itemResponses = items.stream()
                .map(this::toCartItemDetailResponse)
                .toList();

        int totalQuantity = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        BigDecimal totalPrice = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartDetailResponse.builder()
                .items(itemResponses)
                .totalQuantity(totalQuantity)
                .totalPrice(totalPrice)
                .build();
    }
}

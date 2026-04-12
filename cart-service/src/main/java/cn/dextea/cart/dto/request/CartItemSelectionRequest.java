package cn.dextea.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemSelectionRequest {

    @NotNull(message = "客制化分类ID不能为空")
    @Min(value = 1, message = "客制化分类ID不合法")
    private Long itemId;

    @NotNull(message = "客制化选项ID不能为空")
    @Min(value = 1, message = "客制化选项ID不合法")
    private Long optionId;
}

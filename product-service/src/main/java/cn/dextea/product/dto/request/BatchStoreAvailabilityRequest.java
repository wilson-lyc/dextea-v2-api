package cn.dextea.product.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "门店ID不能为空")
    @Min(value = 1, message = "门店ID不合法")
    private Long storeId;

    @NotEmpty(message = "商品列表不能为空")
    @Valid
    private List<ProductAvailabilityItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAvailabilityItem {

        @NotNull(message = "商品ID不能为空")
        @Min(value = 1, message = "商品ID不合法")
        private Long productId;

        @NotNull(message = "选项ID列表不能为空")
        private List<Long> optionIds;
    }
}

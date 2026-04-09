package cn.dextea.product.dto.request;

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
public class UpdateStoreCustomizationOptionSaleRequest {

    @NotNull(message = "门店ID不能为空")
    @Min(value = 1, message = "门店ID无效")
    private Long storeId;

    @NotNull(message = "在售状态不能为空")
    private Boolean onSale;
}

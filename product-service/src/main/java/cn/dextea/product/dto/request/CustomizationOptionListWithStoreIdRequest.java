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
public class CustomizationOptionListWithStoreIdRequest {

    @NotNull(message = "门店ID不能为空")
    @Min(value = 1, message = "门店ID无效")
    private Long storeId;
}

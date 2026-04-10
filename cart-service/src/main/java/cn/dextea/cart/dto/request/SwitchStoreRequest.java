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
public class SwitchStoreRequest {

    @NotNull(message = "门店ID不能为空")
    @Min(value = 1, message = "门店ID不合法")
    private Long storeId;
}

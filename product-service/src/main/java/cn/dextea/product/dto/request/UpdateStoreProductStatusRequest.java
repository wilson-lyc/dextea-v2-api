package cn.dextea.product.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.product.enums.StoreProductSaleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoreProductStatusRequest {

    @NotNull(message = "状态不能为空")
    @EnumValue(enumClass = StoreProductSaleStatus.class, fieldName = "门店商品状态")
    private Integer status;
}

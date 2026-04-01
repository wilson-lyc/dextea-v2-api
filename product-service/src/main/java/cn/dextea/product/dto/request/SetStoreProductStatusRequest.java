package cn.dextea.product.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.common.validation.group.ValidationGroups;
import cn.dextea.product.enums.StoreProductSaleStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SetStoreProductStatusRequest {

    @NotNull(message = "门店ID不能为空", groups = ValidationGroups.Create.class)
    @Min(value = 1, message = "门店ID无效")
    private Long storeId;

    @NotNull(message = "商品ID不能为空", groups = ValidationGroups.Create.class)
    @Min(value = 1, message = "商品ID无效")
    private Long productId;

    @NotNull(message = "销售状态不能为空")
    @EnumValue(enumClass = StoreProductSaleStatus.class, fieldName = "销售状态")
    private Integer status;
}
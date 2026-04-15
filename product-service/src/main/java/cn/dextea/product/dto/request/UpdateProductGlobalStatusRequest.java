package cn.dextea.product.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.product.enums.ProductStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductGlobalStatusRequest {

    @NotNull(message = "商品状态不能为空")
    @EnumValue(enumClass = ProductStatus.class, fieldName = "商品状态")
    private Integer status;
}

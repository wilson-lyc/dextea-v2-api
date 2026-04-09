package cn.dextea.product.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.product.enums.CustomizationStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomizationOptionRequest {

    @NotBlank(message = "选项名称不能为空")
    @Size(max = 64, message = "选项名称长度不能超过64位")
    private String name;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.00", message = "价格不能小于0")
    private BigDecimal price;

    @Min(value = 1, message = "原料ID不合法")
    private Long ingredientId;

    @DecimalMin(value = "0.00", inclusive = false, message = "原料用量必须大于0")
    private BigDecimal ingredientQuantity;

    @NotNull(message = "全局状态不能为空")
    @EnumValue(enumClass = CustomizationStatus.class, fieldName = "全局状态")
    private Integer status;
}

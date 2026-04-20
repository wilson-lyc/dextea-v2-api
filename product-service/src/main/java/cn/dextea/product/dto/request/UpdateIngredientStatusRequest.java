package cn.dextea.product.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.product.enums.IngredientStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateIngredientStatusRequest {

    @NotNull(message = "状态不能为空")
    @EnumValue(enumClass = IngredientStatus.class, fieldName = "原料状态")
    private Integer status;
}

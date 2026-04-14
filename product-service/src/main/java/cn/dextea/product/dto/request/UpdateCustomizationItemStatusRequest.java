package cn.dextea.product.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.product.enums.CustomizationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomizationItemStatusRequest {

    @NotNull(message = "全局状态不能为空")
    @EnumValue(enumClass = CustomizationStatus.class, fieldName = "全局状态")
    private Integer status;
}

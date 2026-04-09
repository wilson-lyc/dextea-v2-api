package cn.dextea.product.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.product.enums.CustomizationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomizationItemRequest {

    @NotBlank(message = "项目名称不能为空")
    @Size(max = 64, message = "项目名称长度不能超过64位")
    private String name;

    @Size(max = 255, message = "项目介绍长度不能超过255位")
    private String description;

    @NotNull(message = "全局状态不能为空")
    @EnumValue(enumClass = CustomizationStatus.class, fieldName = "全局状态")
    private Integer status;
}

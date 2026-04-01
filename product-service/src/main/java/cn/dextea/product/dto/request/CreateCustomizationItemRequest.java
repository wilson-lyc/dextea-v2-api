package cn.dextea.product.dto.request;

import jakarta.validation.constraints.Min;
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
public class CreateCustomizationItemRequest {

    @NotBlank(message = "定制项名称不能为空")
    @Size(max = 64, message = "定制项名称长度不能超过64位")
    private String name;

    @NotNull(message = "排序值不能为空")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sortOrder;

    @NotNull(message = "是否必选不能为空")
    private Boolean isRequired;
}

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
public class BindProductCustomizationItemRequest {

    @NotNull(message = "客制化项目ID不能为空")
    @Min(value = 1, message = "客制化项目ID不能为空")
    private Long itemId;

    @Builder.Default
    private Integer sortOrder = 0;
}

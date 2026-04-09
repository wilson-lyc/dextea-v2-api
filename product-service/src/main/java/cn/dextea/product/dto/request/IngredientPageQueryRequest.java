package cn.dextea.product.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientPageQueryRequest {

    @Min(value = 1, message = "当前页码不能小于1")
    @Builder.Default
    private Long current = 1L;

    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能大于100")
    @Builder.Default
    private Long size = 10L;

    @Size(max = 64, message = "原料名称长度不能超过64位")
    private String name;
}

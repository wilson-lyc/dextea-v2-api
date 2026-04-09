package cn.dextea.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuGroupRequest {

    @NotBlank(message = "分组名称不能为空")
    @Size(max = 64, message = "分组名称长度不能超过64位")
    private String name;

    @NotNull(message = "商品ID列表不能为空")
    private List<Long> productIds;
}

package cn.dextea.product.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.product.enums.StoreProductStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class StoreProductPageRequest {

    @NotNull(message = "门店ID不能为空")
    @Min(value = 1, message = "门店ID无效")
    private Long storeId;

    @Min(value = 1, message = "当前页码不能小于1")
    @Builder.Default
    private Long current = 1L;

    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能大于100")
    @Builder.Default
    private Long size = 10L;

    @Size(max = 100, message = "商品名称长度不能超过100位")
    private String name;

    @EnumValue(enumClass = StoreProductStatus.class, fieldName = "门店在售状态")
    private Integer status;
}

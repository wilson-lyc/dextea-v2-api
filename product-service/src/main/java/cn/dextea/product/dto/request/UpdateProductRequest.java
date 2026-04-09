package cn.dextea.product.dto.request;

import cn.dextea.common.validation.annotation.EnumValue;
import cn.dextea.product.enums.ProductStatus;
import jakarta.validation.constraints.DecimalMin;
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
public class UpdateProductRequest {

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称长度不能超过100位")
    private String name;

    @Size(max = 500, message = "商品简介长度不能超过500位")
    private String description;

    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.01", message = "售价不能小于0.01")
    private BigDecimal price;

    @NotNull(message = "商品状态不能为空")
    @EnumValue(enumClass = ProductStatus.class, fieldName = "商品状态")
    private Integer status;
}

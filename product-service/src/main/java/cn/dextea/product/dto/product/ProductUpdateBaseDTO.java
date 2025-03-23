package cn.dextea.product.dto.product;

import cn.dextea.product.pojo.Product;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateBaseDTO {
    @NotBlank(message = "商品名称不能为空")
    private String name;
    private String description;
    @NotNull(message = "商品价格不能为空")
    private BigDecimal price;
    @NotNull(message = "商品分类不能为空")
    private Long categoryId;
    @Min(value = 0, message = "状态码有误")
    @Max(value = 1, message = "状态码有误")
    @NotNull(message = "全局状态不能为空")
    private Integer globalStatus;

    public Product toProduct() {
        return Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .categoryId(categoryId)
                .globalStatus(globalStatus)
                .build();
    }
}

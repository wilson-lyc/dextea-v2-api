package cn.dextea.product.dto.product;

import cn.dextea.product.pojo.Product;
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
    @NotNull(message = "全局状态不能为空")
    private Integer globalStatus;

    public Product toProduct(Long id) {
        return Product.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .categoryId(categoryId)
                .globalStatus(globalStatus)
                .build();
    }
}

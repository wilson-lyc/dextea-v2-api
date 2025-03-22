package cn.dextea.product.dto;

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
    private String name;//名称
    private String description;//简介
    @NotNull(message = "商品价格不能为空")
    private BigDecimal price;//价格
    @NotNull(message = "商品分类不能为空")
    private Long categoryId;//分类ID

    public Product toProduct() {
        return Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .categoryId(categoryId)
                .build();
    }
}

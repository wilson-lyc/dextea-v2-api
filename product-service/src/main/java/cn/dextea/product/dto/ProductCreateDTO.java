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
public class ProductCreateDTO {
    @NotBlank
    private String name;//名称
    private String description;//描述
    @NotNull
    private BigDecimal price;//价格
    @NotNull
    private Long typeId;//品类ID
    @NotNull
    private Integer status;//状态

    public Product toProduct() {
        return Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .typeId(typeId)
                .status(status)
                .build();
    }
}

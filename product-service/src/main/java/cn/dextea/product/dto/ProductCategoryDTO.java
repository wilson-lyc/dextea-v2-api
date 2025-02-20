package cn.dextea.product.dto;

import cn.dextea.product.pojo.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryDTO {
    @NotBlank
    private String name;

    public ProductCategory toProductType() {
        return ProductCategory.builder()
                .name(this.name)
                .build();
    }
}

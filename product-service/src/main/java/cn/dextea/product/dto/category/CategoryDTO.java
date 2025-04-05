package cn.dextea.product.dto.category;

import cn.dextea.product.pojo.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    @NotBlank
    private String name;

    public ProductCategory toCategory() {
        return ProductCategory.builder()
                .name(this.name)
                .build();
    }
}

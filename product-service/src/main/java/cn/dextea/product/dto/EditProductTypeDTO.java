package cn.dextea.product.dto;

import cn.dextea.product.pojo.ProductType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditProductTypeDTO {
    @NotBlank
    private String name;

    public ProductType toProductType() {
        return ProductType.builder()
                .name(this.name)
                .build();
    }
}

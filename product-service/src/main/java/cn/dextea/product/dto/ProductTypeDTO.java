package cn.dextea.product.dto;

import cn.dextea.product.pojo.ProductType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductTypeDTO {
    @NotBlank
    private String label;

    public ProductType toProductType() {
        return ProductType.builder()
                .label(this.label)
                .build();
    }
}

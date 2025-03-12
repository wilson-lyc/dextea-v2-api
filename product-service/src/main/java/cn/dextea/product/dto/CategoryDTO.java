package cn.dextea.product.dto;

import cn.dextea.product.pojo.Category;
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

    public Category toCategory() {
        return Category.builder()
                .name(this.name)
                .build();
    }
}

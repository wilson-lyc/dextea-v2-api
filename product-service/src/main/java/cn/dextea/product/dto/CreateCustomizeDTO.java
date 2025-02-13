package cn.dextea.product.dto;

import cn.dextea.product.pojo.Customize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomizeDTO {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private Integer sort;
    @NotNull
    private Integer status;
    @NotNull
    private Long productId;

    public Customize toCustomize(){
        return Customize.builder()
                .name(name)
                .description(description)
                .sort(sort)
                .status(status)
                .productId(productId)
                .build();
    }
}

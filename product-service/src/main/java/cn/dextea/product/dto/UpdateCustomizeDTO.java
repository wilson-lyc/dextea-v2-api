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
public class UpdateCustomizeDTO {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private Integer sort;
    @NotNull
    private Integer status;

    public Customize toCustomize(){
        return Customize.builder()
                .name(name)
                .description(description)
                .sort(sort)
                .status(status)
                .build();
    }
}

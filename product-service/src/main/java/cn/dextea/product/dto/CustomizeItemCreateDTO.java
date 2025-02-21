package cn.dextea.product.dto;

import cn.dextea.product.pojo.CustomizeItem;
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
public class CustomizeItemCreateDTO {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private Integer sort;
    @NotNull
    private Integer status;
    @NotNull
    private Long productId;

    public CustomizeItem toCustomize(){
        return CustomizeItem.builder()
                .name(name)
                .description(description)
                .sort(sort)
                .status(status)
                .productId(productId)
                .build();
    }
}

package cn.dextea.product.dto.option;

import cn.dextea.product.pojo.CustomizeOption;
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
@NoArgsConstructor
@AllArgsConstructor
public class CustomizeOptionCreateDTO {
    @NotBlank
    private String name;
    private BigDecimal price;
    @NotNull
    private Integer sort;

    public CustomizeOption toCustomizeOption(){
        return CustomizeOption.builder()
                .name(name)
                .price(price)
                .sort(sort)
                .build();
    }
}

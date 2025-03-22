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
public class OptionCreateDTO {
    @NotBlank(message = "选项名不能为空")
    private String name;
    @NotNull(message = "价格不能为空")
    private BigDecimal price;
    @NotNull(message = "排序不能为空")
    private Integer sort;

    public CustomizeOption toCustomizeOption(){
        return CustomizeOption.builder()
                .name(name)
                .price(price)
                .sort(sort)
                .build();
    }
}

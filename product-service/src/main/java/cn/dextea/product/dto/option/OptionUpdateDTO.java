package cn.dextea.product.dto.option;

import cn.dextea.common.code.CustomizeOptionStatus;
import cn.dextea.common.pojo.CustomizeOption;
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
public class OptionUpdateDTO {
    @NotBlank(message = "选项名不能为空")
    private String name;
    private BigDecimal price;
    @NotNull(message = "排序不能为空")
    private Integer sort;
    @NotNull(message = "全局状态不能为空")
    private CustomizeOptionStatus globalStatus;

    public CustomizeOption toCustomizeOption(){
        return CustomizeOption.builder()
                .name(name)
                .price(price)
                .sort(sort)
                .globalStatus(globalStatus)
                .build();
    }
}

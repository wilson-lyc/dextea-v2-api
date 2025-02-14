package cn.dextea.product.dto;

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
public class CreateCustomizeOptionDTO {
    @NotBlank
    private String name;
    private BigDecimal price;
    @NotNull
    private Integer sort;
    @NotNull
    private Integer status;
    @NotNull
    private Long customizeId;

    public CustomizeOption toCustomizeOption(){
        return CustomizeOption.builder()
                .name(name)
                .price(price)
                .sort(sort)
                .status(status)
                .customizeId(customizeId)
                .build();
    }
}

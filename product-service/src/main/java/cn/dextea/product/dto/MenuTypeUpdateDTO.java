package cn.dextea.product.dto;

import cn.dextea.product.pojo.MenuType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuTypeUpdateDTO {
    @NotBlank
    private String name;

    public MenuType toMenuType() {
        return MenuType.builder()
                .name(name)
                .build();
    }
}

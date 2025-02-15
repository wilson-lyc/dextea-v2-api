package cn.dextea.product.dto;

import cn.dextea.product.pojo.MenuType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuTypeCreateDTO {
    @NotBlank
    private String name;
    @NotNull
    private Long menuId;

    public MenuType toMenuType() {
        return MenuType.builder()
                .name(name)
                .menuId(menuId)
                .build();
    }
}

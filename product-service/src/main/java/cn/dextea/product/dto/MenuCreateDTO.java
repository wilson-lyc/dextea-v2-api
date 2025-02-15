package cn.dextea.product.dto;

import cn.dextea.product.pojo.Menu;
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
public class MenuCreateDTO {
    @NotBlank(message = "菜单名称不能为空")
    private String name;
    private String description;
    public Menu toMenu() {
        return Menu.builder()
                .name(name)
                .description(description)
                .build();
    }
}

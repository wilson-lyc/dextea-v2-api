package cn.dextea.menu.dto.menu;

import cn.dextea.common.pojo.Menu;
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
public class MenuUpdateBaseDTO {
    @NotBlank(message = "菜单名称不能为空")
    private String name;
    private String description;

    public Menu toMenu(Long id){
        return Menu.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
    }
}

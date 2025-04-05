package cn.dextea.menu.dto.menu;

import cn.dextea.menu.pojo.Menu;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuCreateDTO {
    @NotBlank(message = "菜单名不能为空")
    private String name;
    private String description;

    public Menu toMenu(){
        return Menu.builder()
                .name(name)
                .description(description)
                .content(new ArrayList<>())
                .build();
    }
}

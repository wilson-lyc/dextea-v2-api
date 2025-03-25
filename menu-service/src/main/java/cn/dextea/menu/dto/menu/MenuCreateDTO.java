package cn.dextea.menu.dto.menu;

import cn.dextea.common.pojo.Menu;
import cn.dextea.common.pojo.MenuGroup;
import com.alibaba.fastjson2.JSONArray;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

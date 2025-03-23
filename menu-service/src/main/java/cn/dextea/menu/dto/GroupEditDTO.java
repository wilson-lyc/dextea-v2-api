package cn.dextea.menu.dto;

import cn.dextea.menu.pojo.MenuGroup;
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
public class GroupEditDTO {
    @NotBlank(message = "名称不能为空")
    private String name;
    @NotNull(message = "优先级不能为空")
    private Integer sort;
    public MenuGroup toMenuGroup() {
        return MenuGroup.builder()
                .name(name)
                .sort(sort)
                .build();
    }
}

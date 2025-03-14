package dto;

import cn.dextea.product.pojo.MenuGroup;
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
public class MenuGroupCreateDTO {
    @NotBlank(message = "名称不能为空")
    private String name;
    @NotNull(message = "菜单id不能为空")
    private Long menuId;
    @NotNull(message = "优先级不能为空")
    private Integer sort;
    public MenuGroup toMenuGroup() {
        return MenuGroup.builder()
                .name(name)
                .menuId(menuId)
                .sort(sort)
                .build();
    }
}

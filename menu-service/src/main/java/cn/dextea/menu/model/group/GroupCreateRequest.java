package cn.dextea.menu.model.group;

import cn.dextea.menu.pojo.MenuGroup;
import cn.dextea.menu.pojo.MenuProduct;
import cn.hutool.core.util.IdUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class GroupCreateRequest {
    @NotBlank(message = "分组名不能为空")
    private String name;
    @NotNull(message = "排序不能为空")
    private Integer sort;

    public MenuGroup toMenuGroup(){
        return MenuGroup.builder()
                .id(IdUtil.objectId())
                .name(name)
                .sort(sort)
                .content(new ArrayList<MenuProduct>())
                .build();
    }
}

package cn.dextea.menu.dto.group;

import cn.dextea.common.pojo.Menu;
import cn.dextea.common.pojo.MenuGroup;
import cn.dextea.menu.dto.menu.MenuBaseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupListDTO {
    private String id;
    private String name;
    private Integer sort;
    public static GroupListDTO fromMenuGroup(MenuGroup menuGroup) {
        return GroupListDTO.builder()
                .id(menuGroup.getId())
                .name(menuGroup.getName())
                .sort(menuGroup.getSort())
                .build();
    }
}

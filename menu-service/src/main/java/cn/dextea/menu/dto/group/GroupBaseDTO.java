package cn.dextea.menu.dto.group;

import cn.dextea.menu.pojo.MenuGroup;
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
public class GroupBaseDTO {
    private String id;
    private String name;
    private Integer sort;
    public static GroupBaseDTO fromMenuGroup(MenuGroup menuGroup) {
        return GroupBaseDTO.builder()
                .id(menuGroup.getId())
                .name(menuGroup.getName())
                .sort(menuGroup.getSort())
                .build();
    }
}

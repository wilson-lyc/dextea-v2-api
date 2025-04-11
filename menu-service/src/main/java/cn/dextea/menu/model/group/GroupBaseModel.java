package cn.dextea.menu.model.group;

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
public class GroupBaseModel {
    private String id;
    private String name;
    private Integer sort;
    public static GroupBaseModel fromMenuGroup(MenuGroup menuGroup) {
        return GroupBaseModel.builder()
                .id(menuGroup.getId())
                .name(menuGroup.getName())
                .sort(menuGroup.getSort())
                .build();
    }
}

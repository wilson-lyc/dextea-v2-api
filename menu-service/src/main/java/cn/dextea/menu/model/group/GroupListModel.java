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
public class GroupListModel {
    private String id;
    private String name;
    private Integer sort;
    public static GroupListModel fromMenuGroup(MenuGroup menuGroup) {
        return GroupListModel.builder()
                .id(menuGroup.getId())
                .name(menuGroup.getName())
                .sort(menuGroup.getSort())
                .build();
    }
}

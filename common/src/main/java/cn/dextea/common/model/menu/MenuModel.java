package cn.dextea.common.model.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuModel {
    private Long id;
    private String name;
    private String description;
    private List<MenuGroupModel> content;
    private String createTime;
    private String updateTime;
}

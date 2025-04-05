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
public class MenuGroupModel {
    private String id;
    private String name;
    private Integer sort;
    private List<MenuProductModel> content;
}

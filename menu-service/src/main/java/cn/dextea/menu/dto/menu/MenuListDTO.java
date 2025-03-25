package cn.dextea.menu.dto.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuListDTO {
    private Long id;
    private String name;
    private String description;
    private String createTime;
    private String updateTime;
}

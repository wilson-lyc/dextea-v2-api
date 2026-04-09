package cn.dextea.menu.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
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
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "s_menu",autoResultMap = true)
public class Menu {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    @TableField(value = "content",typeHandler = Fastjson2TypeHandler.class)
    private List<MenuGroup> content;
    private String createTime;
    private String updateTime;

    public void sortContent(){
        content.sort((o1, o2) -> o1.getSort() - o2.getSort());
    }

    public MenuGroup getMenuGroup(String id){
        for (MenuGroup menuGroup : content) {
            if (menuGroup.getId().equals(id)) {
                return menuGroup;
            }
        }
        return null;
    }
}

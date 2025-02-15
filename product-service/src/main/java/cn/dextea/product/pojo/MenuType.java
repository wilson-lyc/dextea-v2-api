package cn.dextea.product.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("s_menu_type")
public class MenuType {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long menuId;
    private String createTime;
    private String updateTime;
}

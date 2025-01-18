package cn.dextea.auth.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("d_router")
public class Router {
    private Integer id;
    private String type;
    private String path;
    private String label;
    private String icon;
    private Integer parentId;
}

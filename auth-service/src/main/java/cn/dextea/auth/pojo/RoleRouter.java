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
@TableName("r_role_router")
public class RoleRouter {
    private Long roleId;
    private Long routerId;
    private String createTime;
}

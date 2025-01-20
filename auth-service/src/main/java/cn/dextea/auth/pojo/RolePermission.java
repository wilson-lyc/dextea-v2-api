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
@TableName("r_role_permission")
public class RolePermission {
    private Long roleId;
    private Long permissionId;
    private String createTime;
}

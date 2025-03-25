package cn.dextea.common.pojo;

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
@TableName("r_staff_role")
public class StaffRole {
    private Long staffId;
    private Long roleId;
    private String createTime;
}

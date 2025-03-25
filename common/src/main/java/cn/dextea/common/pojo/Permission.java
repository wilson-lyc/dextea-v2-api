package cn.dextea.common.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("d_permission")
public class Permission {
    private Long id;
    @TableField("`key`")
    private String key;
    private String description;
}

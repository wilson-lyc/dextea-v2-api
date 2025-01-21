package cn.dextea.auth.pojo;

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
@TableName(value = "s_role",autoResultMap = true)
public class Role {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("`key`")
    private String key;
    private String description;
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<Integer> routers;
    private String createTime;
}

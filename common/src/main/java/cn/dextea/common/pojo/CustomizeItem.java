package cn.dextea.common.pojo;

import cn.dextea.common.code.CustomizeItemStatus;
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
@AllArgsConstructor
@NoArgsConstructor
@TableName("s_customize_item")
public class CustomizeItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer sort;
    private CustomizeItemStatus status;
    private Long productId;
    private String createTime;
    private String updateTime;
}

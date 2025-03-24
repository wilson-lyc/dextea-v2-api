package cn.dextea.common.pojo;

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
@TableName("s_product_category")
public class ProductCategory {
    @TableId(type = IdType.AUTO)
    private Long id;//分类ID
    private String name;//分类名称
    private String createTime;
    private String updateTime;
}

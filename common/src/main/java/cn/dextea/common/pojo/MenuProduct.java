package cn.dextea.common.pojo;

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
@TableName("r_menu_group_product")
public class MenuProduct {
    private Long groupId;
    private Long productId;
    private Integer sort;
    private String createTime;
}

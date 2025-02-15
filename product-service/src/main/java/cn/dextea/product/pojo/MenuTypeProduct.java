package cn.dextea.product.pojo;

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
@TableName("r_menu_type_product")
public class MenuTypeProduct {
    private Long menuTypeId;
    private Long productId;
    private String createTime;
}

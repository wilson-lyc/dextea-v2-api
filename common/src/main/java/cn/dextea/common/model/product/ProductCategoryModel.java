package cn.dextea.common.model.product;

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
public class ProductCategoryModel {
    private Long id;
    private String name;
    private String createTime;
    private String updateTime;
}

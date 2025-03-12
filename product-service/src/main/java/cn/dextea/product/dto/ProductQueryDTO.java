package cn.dextea.product.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductQueryDTO {
    private Long id;// 商品ID
    private String name;// 商品名称
    private Integer status;// 状态
    private Integer categoryId;// 分类ID
    private Double minPrice;// 最小价格
    private Double maxPrice;// 最大价格
    private Long storeId;// 店铺ID
}

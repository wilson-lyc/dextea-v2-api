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
    @Min(value = 1,message = "current不能小于1")
    private int current;
    @Min(value = 1,message = "size不能小于1")
    private int size;
    private Long id;// 商品ID
    private String name;// 商品名称
    private Integer status;// 状态
    private Integer categoryId;// 分类ID
}

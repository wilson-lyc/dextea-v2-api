package cn.dextea.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListDTO {
    private Long id;//商品ID
    private String name;//名称
    private String description;//描述
    private BigDecimal price;//价格
    private Long categoryId;//分类ID
    private String categoryName;//分类名称
    private Integer globalStatus;//全局状态
    private Integer storeStatus;//门店状态
    private Integer status;//销售状态

    public Integer getStatus() {
        if(globalStatus == 0){
            return 0;
        }else if(Objects.isNull(storeStatus)){
            return globalStatus;
        }else{
            return storeStatus;
        }
    }
}

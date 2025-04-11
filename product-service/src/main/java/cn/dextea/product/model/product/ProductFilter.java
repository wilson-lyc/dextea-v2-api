package cn.dextea.product.model.product;

import cn.dextea.common.code.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilter {
    private Long id;// 商品ID
    private String name;// 商品名称
    private Integer status;// 状态
    private Integer globalStatus;// 全局状态
    private Integer storeStatus;//门店状态
    private Integer categoryId;// 分类ID
    private Double minPrice;// 最小价格
    private Double maxPrice;// 最大价格

    public Integer getGlobalStatus(){
        if (Objects.isNull(status))
            return null;
        if(status == ProductStatus.GLOBAL_FORBIDDEN.getValue())
            return ProductStatus.GLOBAL_FORBIDDEN.getValue();
        else
            return ProductStatus.AVAILABLE.getValue();
    }

    public Integer getStoreStatus(){
        if (Objects.isNull(status))
            return null;
        if(status == ProductStatus.GLOBAL_FORBIDDEN.getValue())
            return null;
        else
            return status;
    }
}

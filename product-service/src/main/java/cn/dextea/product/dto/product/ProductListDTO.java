package cn.dextea.product.dto.product;

import cn.dextea.common.code.ProductStatus;
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
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
    private ProductStatus globalStatus;
    private ProductStatus storeStatus;
    private ProductStatus status;

    public ProductStatus getStatus() {
        return ProductStatus.getStatus(globalStatus,storeStatus);
    }
}

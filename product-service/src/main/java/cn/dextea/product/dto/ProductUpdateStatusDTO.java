package cn.dextea.product.dto;

import cn.dextea.product.pojo.Product;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateStatusDTO {
    @NotNull(message = "status不能为空")
    private Integer status;

    public Product toProduct(){
        return Product.builder()
                .status(this.status)
                .build();
    }
}

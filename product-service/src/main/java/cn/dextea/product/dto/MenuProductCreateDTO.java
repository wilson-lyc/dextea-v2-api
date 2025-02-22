package cn.dextea.product.dto;

import cn.dextea.product.pojo.MenuProduct;
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
public class MenuProductCreateDTO {
    @NotNull(message = "groupId不能为空")
    private Long groupId;
    @NotNull(message = "productId不能为空")
    private Long productId;
    @NotNull(message = "sort不能为空")
    private Integer sort;

    public MenuProduct toMenuProduct(){
        return MenuProduct.builder()
                .groupId(groupId)
                .productId(productId)
                .sort(sort)
                .build();
    }
}

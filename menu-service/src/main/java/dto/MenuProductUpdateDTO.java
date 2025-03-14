package dto;

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
public class MenuProductUpdateDTO {
    @NotNull(message = "sort不能为空")
    private Integer sort;

    public MenuProduct toMenuProduct(){
        return MenuProduct.builder()
                .sort(sort)
                .build();
    }
}

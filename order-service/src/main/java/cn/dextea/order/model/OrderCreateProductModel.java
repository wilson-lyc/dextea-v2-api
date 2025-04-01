package cn.dextea.order.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateProductModel {
    @NotNull(message = "productId不能为空")
    private Long id;
    private String name;
    @Valid
    private List<ProductCustomizeModel> customize;
    @NotNull(message = "count不能为空")
    private Integer count;
    @NotBlank(message = "skuId不能为空")
    private String skuId;
}

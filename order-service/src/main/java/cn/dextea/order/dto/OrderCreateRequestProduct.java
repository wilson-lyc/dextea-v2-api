package cn.dextea.order.dto;

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
public class OrderCreateRequestProduct {
    @NotNull(message = "productId不能为空")
    private Long id;
    @NotNull(message = "productName不能为空")
    private String name;
    @NotBlank(message = "productCover不能为空")
    private String cover;
    @Valid @NotNull(message = "productCustomize不能为空")
    private List<OrderCreateRequestProductCustomize> customize;
    @NotNull(message = "productCount不能为空")
    private Integer count;
}

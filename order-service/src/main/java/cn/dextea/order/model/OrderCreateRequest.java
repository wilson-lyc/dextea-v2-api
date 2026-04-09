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
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    @NotNull(message = "customerId不能为空")
    private Long customerId;
    @NotNull(message = "storeId不能为空")
    private Long storeId;
    @NotBlank(message = "storeName不能为空")
    private String storeName;
    @NotNull(message = "dineMode不能为空")
    private Integer dineMode;
    @Valid @NotNull(message = "products不能为空")
    private List<OrderCreateRequestProduct> products;
}

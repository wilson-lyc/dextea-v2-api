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
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    private Long customerId;// 顾客ID
    @NotNull(message = "storeId不能为空")
    private Long storeId;// 门店ID
    @NotBlank(message = "storeName不能为空")
    private String storeName;// 门店名称
    @NotNull(message = "dineMode不能为空")
    private Integer dineMode;// 用餐方式
    private String tableNo;// 桌号
    @Valid @NotNull(message = "products不能为空")
    private List<OrderCreateRequestProduct> products;// 商品列表
}

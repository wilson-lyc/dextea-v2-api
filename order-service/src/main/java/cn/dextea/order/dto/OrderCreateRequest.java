package cn.dextea.order.dto;

import cn.dextea.order.model.OrderCreateProductModel;
import jakarta.validation.Valid;
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
    private Long customerId;// 顾客ID
    @NotNull(message = "storeId不能为空")
    private Long storeId;// 门店ID
    @NotNull(message = "dineMode不能为空")
    private Integer dineMode;// 用餐方式
    private String tableNo;// 桌号
    @NotNull(message = "products不能为空")
    @Valid
    private List<OrderCreateProductModel> products;// 商品列表
}

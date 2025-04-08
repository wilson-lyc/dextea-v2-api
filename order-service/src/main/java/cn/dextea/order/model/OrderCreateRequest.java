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
    private Long customerId;// 顾客ID
    @NotNull(message = "缺少门店ID")
    private Long storeId;// 门店ID
    @NotBlank(message = "缺少门店名称")
    private String storeName;// 门店名称
    @NotNull(message = "缺少用餐方式")
    private Integer dineMode;// 用餐方式
    @Valid @NotNull(message = "缺少商品列表")
    private List<OrderCreateRequestProduct> products;// 商品列表
}

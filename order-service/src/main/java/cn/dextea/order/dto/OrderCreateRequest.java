package cn.dextea.order.dto;

import cn.dextea.common.pojo.CartItem;
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
    private Long storeId;// 门店ID
    private Integer dineMode;// 用餐方式
    private String tableNo;// 桌号
    private List<CartItem> cart;// 购物车
}

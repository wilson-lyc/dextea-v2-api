package cn.dextea.common.model.order;

import cn.dextea.common.code.DineMode;
import cn.dextea.common.code.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderModel {
    // 编号
    private String id;
    private String tradeNo;
    // 门店
    private Long storeId;
    private String storeName;
    // 顾客
    private Long customerId;
    // 用餐方式
    private Integer dineMode;
    private String dineModeText;
    // 用餐信息
    private String tableNo;
    private String pickUpNo;
    // 商品
    List<OrderProductModel> products;
    // 订单统计信息
    private Integer totalCount;
    private BigDecimal totalPrice;
    // 状态
    private Integer status;
    private String statusText;
    // 时间
    private String createTime;
    private String payTime;
    private String payExpireTime;
    private String updateTime;

    public String getDineModeText() {
        return DineMode.fromValue(dineMode).getLabel();
    }

    public Integer getStatus() {
        return OrderStatus.fromValue(status,payExpireTime).getValue();
    }

    public String getStatusText() {
        return OrderStatus.fromValue(getStatus()).getLabel();
    }
}

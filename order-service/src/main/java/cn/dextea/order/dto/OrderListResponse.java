package cn.dextea.order.dto;

import cn.dextea.common.code.DineMode;
import cn.dextea.common.code.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResponse {
    private String id;
    private Long customerId;
    private Long storeId;
    private Integer dineMode;
    private String dineModeLabel;
    private String tableNo;
    private String pickUpNo;
    private Integer totalCount;
    private BigDecimal totalPrice;
    private String payNo;
    private Integer status;
    private String statusLabel;
    private String createTime;
    private String payTime;
    private String updateTime;

    public String getDineModeLabel() {
        return DineMode.fromValue(dineMode).getLabel();
    }

    public String getStatusLabel() {
        return OrderStatus.fromValue(status).getLabel();
    }
}

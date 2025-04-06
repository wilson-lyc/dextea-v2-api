package cn.dextea.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderQueryRequest {
    private String id;
    private String tradeNo;
    private Long customerId;
    private Long storeId;
    private Integer dineMode;
    private String pickUpNo;
    private Integer status;
}

package cn.dextea.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderFilter {
    private String id;
    private String tradeNo;
    private Long customerId;
    private Long storeId;
    private Integer dineMode;
    private String pickUpNo;
    private Integer status;
}

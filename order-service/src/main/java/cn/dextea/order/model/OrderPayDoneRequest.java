package cn.dextea.order.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPayDoneRequest {
    @NotBlank(message = "缺少订单编号")
    private String orderId;
    @NotBlank(message = "缺少交易编号")
    private String tradeNo;
}

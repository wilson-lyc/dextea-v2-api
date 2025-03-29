package cn.dextea.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayCreateRequest {
    @NotBlank(message = "orderId不能为空")
    private String orderId;// 订单id
    @NotBlank(message = "buyerOpenId不能为空")
    private String buyerOpenId;// 支付宝用户id
    @NotNull(message = "totalPrice不能为空")
    private BigDecimal totalPrice;// 订单总金额
    @NotBlank(message = "subject不能为空")
    private String subject;// 订单标题
}

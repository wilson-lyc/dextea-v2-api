package cn.dextea.order.dto;

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
    private String orderId;
    @NotBlank(message = "tradeNo不能为空")
    private String tradeNo;
}

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
public class OrderPayCancelRequest {
    @NotBlank(message = "orderId不能为空")
    private String orderId;
}

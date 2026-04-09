package cn.dextea.order.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRefundRequest {
    @NotBlank(message = "orderId不能为空")
    private String orderId;
    private Long staffId;
    @NotBlank(message = "操作者密码不能为空")
    private String password;
}

package cn.dextea.product.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdjustInventoryRequest {

    /**
     * Stock adjustment delta.
     * Positive value = restock; negative value = consumption.
     * The resulting quantity must not fall below zero.
     */
    @NotNull(message = "调整量不能为空")
    private BigDecimal delta;

    /** Optional reason for this adjustment, for audit trail purposes. */
    @Size(max = 128, message = "调整原因不能超过128位")
    private String reason;
}

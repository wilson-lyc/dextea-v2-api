package cn.dextea.cart.dto.response;

import cn.dextea.cart.enums.RemoveReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemovedCartItemResponse {

    private String skuId;
    private String productName;
    private RemoveReason reason;
}

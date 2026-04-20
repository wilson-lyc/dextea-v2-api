package cn.dextea.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCustomizationItemDetailResponse {

    private Long bindingId;
    private Long itemId;
    private String itemName;
    private String itemDescription;
    private Integer itemStatus;
    private Integer sortOrder;
    private LocalDateTime createTime;
}

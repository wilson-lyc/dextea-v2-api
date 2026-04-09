package cn.dextea.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomizationItemBizDetailResponse {

    private Long id;
    private String name;
    private String description;
    private Integer storeStatus;
    private List<CustomizationOptionBizDetailResponse> options;
}

package cn.dextea.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomizationItemDetailResponse {

    private Long id;
    private Long productId;
    private String name;
    private Integer sortOrder;
    private Boolean isRequired;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<CustomizationOptionDetailResponse> options;
}

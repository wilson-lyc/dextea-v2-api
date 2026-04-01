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
public class CreateCustomizationItemResponse {

    private Long id;
    private Long productId;
    private String name;
    private Integer sortOrder;
    private Boolean isRequired;
    private Integer status;
    private LocalDateTime createTime;
}
